package com.example.e_commerce.datasource.dbservice

import android.content.ContentValues.TAG
import android.util.Log
import com.example.e_commerce.datasource.models.*
import com.example.e_commerce.state.DataState
import com.example.e_commerce.utils.PrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FireBaseRepository
@Inject
constructor(
    private val firestoreDB: FirebaseFirestore
) {
    suspend fun addCategory(category: Categories) {
        firestoreDB.collection("Categories").document(category.id!!).set(category)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${category.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    suspend fun getAllCategories(): Flow<DataState<List<Categories>>> = flow {
        emit(DataState.Loading)
        try {
            val data = firestoreDB.collection("Categories").get().await()
            emit(DataState.Success(data.documents.mapNotNull { it.toObject(Categories::class.java) }))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<List<Categories>>)
        }

    }

    suspend fun getCategory(category: Categories): Categories? {
        return firestoreDB.collection("Categories").document(category.id!!)
            .get().result!!.toObject(Categories::class.java)
    }

    suspend fun addProduct(product: Products) {
        firestoreDB.collection("Categories").document(product.catId!!).collection("Products")
            .add(product)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                product.id = documentReference.id
                runBlocking { updateProduct(product) }

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    suspend fun updateProduct(product: Products): Flow<DataState<Products>> = flow {
        try {
            firestoreDB.collection("Categories").document(product.catId!!)
                .collection("Products").document(product.id!!).set(
                    product,
                    SetOptions.merge()
                ).await()
            Log.d("ContentValues.TAG", "fdf")
            emit(DataState.Success(product))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Products>)
        }
    }

    suspend fun getProductsByCategory(category: Categories): Flow<DataState<List<Products>>> =
        flow {
            emit(DataState.Loading)
            var dataStateCurrentOrder: DataState<Orders>? = null
            getCurrentOrder().collectLatest { dataStateCurrentOrder = it }
            try {
                val docs = firestoreDB.collection("Categories").document(category.id!!)
                    .collection("Products").get().await()
                var listProducts: MutableList<Products>? = mutableListOf()
                when (dataStateCurrentOrder) {
                    is DataState.Success<Orders> -> {
                        for (document in docs.documents) {
                            val product = document.toObject(Products::class.java)

                            getOrderDetails(
                                product!!.id!!,
                                (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!
                            ).collectLatest {
                                when (it) {
                                    is DataState.Error<*> -> {
                                        if (product.quantity > 0)
                                            listProducts!!.add(product)
                                    }
                                }
                            }
                        }
                    }
                    is DataState.Error<*> -> {
                        listProducts = docs.mapNotNull {
                            it.toObject(Products::class.java)
                        }.toMutableList()
                    }
                }
                listProducts!!.filter { it.quantity > 0 }.toMutableList()
                emit(DataState.Success(listProducts) as DataState<List<Products>>)
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<List<Products>>)
            }
        }

    suspend fun getProductsShoppingCartByCategory(category: Categories): Flow<DataState<HashMap<Products, OrderDetails>>> =
        flow {
            emit(DataState.Loading)
            var dataStateCurrentOrder: DataState<Orders>? = null
            getCurrentOrder().collectLatest { dataStateCurrentOrder = it }
            try {
                val docs = firestoreDB.collection("Categories").document(category.id!!)
                    .collection("Products").get().await()
                var listProducts: HashMap<Products, OrderDetails>? = hashMapOf()
                when (dataStateCurrentOrder) {
                    is DataState.Success<Orders> -> {
                        for (document in docs.documents) {
                            val product = document.toObject(Products::class.java)
                            getOrderDetails(
                                product!!.id!!,
                                (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!
                            ).collectLatest {
                                when (it) {
                                    is DataState.Success<OrderDetails> -> {
                                        if (product.quantity > 0)
                                            listProducts!![product] = it.data
                                    }
                                }
                            }
                        }
                    }
                }
                emit(DataState.Success(listProducts) as DataState<HashMap<Products, OrderDetails>>)
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<HashMap<Products, OrderDetails>>)
            }
        }

    suspend fun incrementQuantity(product: Products): Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading)
        try {
            if (product.quantity > 0) {
                product.quantity--
                updateProduct(product)
                var dataStateCurrentOrder: DataState<Orders>? = null
                getCurrentOrder().collectLatest { dataStateCurrentOrder = it }
                var dataStateOrderDetails: DataState<OrderDetails>? = null
                getOrderDetails(
                    product.id!!,
                    (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!
                ).collectLatest {
                    dataStateOrderDetails = it
                }
                (dataStateOrderDetails as DataState.Success<OrderDetails>).data.quantity++
                updateOrderDetails((dataStateOrderDetails as DataState.Success<OrderDetails>).data).collect()
                emit(DataState.Success(true) as DataState<Boolean>)
            }
            Log.d(
                "ContentValues.TAG", "DocumentSnapshot written with dededdedd:"
            )
            emit(DataState.Success(false) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }

    suspend fun decrementQuantity(product: Products): Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading)
        try {
            var dataStateCurrentOrder: DataState<Orders>? = null
            getCurrentOrder().collectLatest { dataStateCurrentOrder = it }
            var dataStateOrderDetails: DataState<OrderDetails>? = null
            getOrderDetails(
                product.id!!,
                (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!
            ).collectLatest {
                dataStateOrderDetails = it
                true
            }
            if ((dataStateOrderDetails as DataState.Success<OrderDetails>).data.quantity > 1) {
                (dataStateOrderDetails as DataState.Success<OrderDetails>).data.quantity--
                product.quantity++
                updateProduct(product)
                updateOrderDetails((dataStateOrderDetails as DataState.Success<OrderDetails>).data).collect()
                emit(DataState.Success(true) as DataState<Boolean>)
            }
            emit(DataState.Success(false) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }


    suspend fun addToShoppingCart(product: Products): Flow<DataState<Products>> = flow {
        emit(DataState.Loading)
        try {
            val data = firestoreDB.collection("Orders").whereEqualTo("submitted", false)
                .get().await()
            if (!data.isEmpty && data.documents[0].exists()) {
                //add in exist order
                addOrderDetails(
                    OrderDetails(
                        data.documents[0].toObject(Orders::class.java)!!.id,
                        product.id,
                        1
                    )
                ).collect()
            } else {
                //add in new order
                var dataStateCurrentOrder: DataState<Orders>? = null
                addOrder(
                    Orders(
                        customerId = PrefManager.getCustomer()!!.id
                    )
                ).collectLatest { dataStateCurrentOrder = it }
                addOrderDetails(
                    OrderDetails(
                        (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!,
                        product.id,
                        1
                    )
                ).collect()
            }
            if (product.quantity > 0) {
                product.quantity--
                updateProduct(product).collect()
            }
            emit(DataState.Success(product))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Products>)
        }
    }

    suspend fun removeFromShoppingCart(
        product: Products,
        orderDetails: OrderDetails
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading)
        try {
            product.quantity += orderDetails.quantity

            removeOrderDetails(orderDetails).collect()
            updateProduct(product).collect()
            var dataStateCurrentOrder: DataState<Orders>? = null
            getCurrentOrder().collectLatest { dataStateCurrentOrder = it }

            var dataStateCheckOrder: DataState<Boolean>? = null
            checkExistOrder(
                (dataStateCurrentOrder as DataState.Success<Orders>).data.id!!
            ).collectLatest {
                dataStateCheckOrder = it
            }
            when (dataStateCheckOrder) {
                is DataState.Success<Boolean> -> {
                    if (!(dataStateCheckOrder as DataState.Success<Boolean>).data)
                        removeOrder((dataStateCurrentOrder as DataState.Success<Orders>).data)
                }
            }
            emit(DataState.Success(true))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }

    private fun addOrderDetails(orderDetails: OrderDetails): Flow<DataState<OrderDetails>> = flow {
        try {
            firestoreDB.collection("OrderDetails")
                .document(orderDetails.orderId + "," + orderDetails.productId).set(orderDetails)
                .await()
            emit(DataState.Success(orderDetails) as DataState<OrderDetails>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<OrderDetails>)
        }
    }

    suspend fun getOrderDetails(productId: String, orderId: String): Flow<DataState<OrderDetails>> =
        flow {
            try {
                val data = firestoreDB.collection("OrderDetails").document("$orderId,$productId")
                    .get().await()
                if (data.exists()) {

                    Log.d(
                        "ContentValues.TAG", "DocumentSnapshot written with ID:"
                    )
                    emit(DataState.Success(data.toObject(OrderDetails::class.java)) as DataState<OrderDetails>)

                } else {
                    Log.d(
                        "ContentValues.TAG", "DocumentSnapshot eeeeeeeeee with ID:"
                    )
                    emit(DataState.Error<Any>(Exception("invalid document")) as DataState<OrderDetails>)

                }
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<OrderDetails>)
            }

        }

    suspend fun updateOrderDetails(orderDetails: OrderDetails): Flow<DataState<OrderDetails>> =
        flow {
            try {
                firestoreDB.collection("OrderDetails")
                    .document(orderDetails.orderId + "," + orderDetails.productId).set(
                        orderDetails,
                        SetOptions.merge()
                    ).await()
                emit(DataState.Success(orderDetails) as DataState<OrderDetails>)
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<OrderDetails>)
            }
        }

    suspend fun checkExistOrder(orderId: String): Flow<DataState<Boolean>> = flow {
        try {
            val data = firestoreDB.collection("OrderDetails")
                .document(orderId).get().await()
            if (data.exists()) {
                emit(DataState.Success(true) as DataState<Boolean>)
            } else
                emit(DataState.Success(false) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Success(false) as DataState<Boolean>)
        }
    }

    private fun removeOrderDetails(orderDetails: OrderDetails): Flow<DataState<OrderDetails>> =
        flow {
            try {
                firestoreDB.collection("OrderDetails")
                    .document(orderDetails.orderId + "," + orderDetails.productId).delete().await()
                emit(DataState.Success(orderDetails))
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<OrderDetails>)
            }
        }

    private suspend fun addOrder(order: Orders): Flow<DataState<Orders>> = flow {
        Log.d(
            "ContentValues.TAG", "DocumentSnapshot written with dededdedd:"
        )
        try {
            Log.d(
                "ContentValues.TAG", "DocumentSnapshot written with dededdedd:1111111111"
            )
            val data = firestoreDB.collection("Orders").add(order).await()
            order.id = data.id
            firestoreDB.collection("Orders").document(data.id).set(
                order,
                SetOptions.merge()
            ).await()
            emit(DataState.Success(order) as DataState<Orders>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Orders>)
        }

    }

    suspend fun getCurrentOrder(): Flow<DataState<Orders>> = flow {
        emit(DataState.Loading)
        try {
            val data = firestoreDB.collection("Orders").whereEqualTo("submitted", false)
                .whereEqualTo("customerId", PrefManager.getCustomer()!!.id)
                .get().await()
            Log.d("hohohohohoh", ": ${data.documents.size}")
            if (data.documents.isNotEmpty())
                emit(DataState.Success(data.documents[0].toObject(Orders::class.java)) as DataState<Orders>)
            else
                emit(DataState.Error<Any>(Exception("error")) as DataState<Orders>)

        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Orders>)
        }
    }

    suspend fun getAllOrders(): Flow<DataState<List<Orders>>> = flow {
        emit(DataState.Loading)
        try {
            val data = firestoreDB.collection("Orders")
                .whereEqualTo("customerId", PrefManager.getCustomer()!!.id)
                .get().await()
            Log.d("rerrererr", ": ${data.documents.size}")
            emit(DataState.Success(data.documents.mapNotNull { it.toObject(Orders::class.java) }))
        } catch (e: Exception) {
            Log.d("hohohohohoh", ": ${e.message}")
            emit(DataState.Error<Any>(e) as DataState<List<Orders>>)
        }
    }

    suspend fun updateOrder(orders: Orders): Flow<DataState<Orders>> = flow {
        emit(DataState.Loading)
        try {
            firestoreDB.collection("Orders")
                .document(orders.id!!).set(
                    orders,
                    SetOptions.merge()
                ).await()
            emit(DataState.Success(orders))

        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Orders>)
        }
    }

    suspend fun removeOrder(order: Orders) {
        firestoreDB.collection("Orders")
            .document(order.id!!).delete().await()
    }

    suspend fun getOrdersDetails(
        orders: List<Orders>,
        category: Categories
    ): Flow<DataState<HashMap<Orders, HashMap<Products, OrderDetails>>>> =
        flow {
            emit(DataState.Loading)
            try {
                val docs = firestoreDB.collection("Categories").document(category.id!!)
                    .collection("Products").get().await()
                var list: HashMap<Orders, HashMap<Products, OrderDetails>> = hashMapOf()
                for (order in orders) {
                    val listProducts: HashMap<Products, OrderDetails> = hashMapOf()
                    for (document in docs.documents) {
                        val product = document.toObject(Products::class.java)
                        getOrderDetails(
                            product!!.id!!,
                            order.id!!
                        ).collectLatest {
                            when (it) {
                                is DataState.Success<OrderDetails> -> {
                                    if (product.quantity > 0) {
                                        listProducts[product] = it.data
                                    }
                                }
                            }
                        }
                    }
                    list[order] = listProducts
                }
                emit(DataState.Success(list) as DataState<HashMap<Orders, HashMap<Products, OrderDetails>>>)
            } catch (e: Exception) {
                emit(DataState.Error<Any>(e) as DataState<HashMap<Orders, HashMap<Products, OrderDetails>>>)
            }
        }

    suspend fun getCustomer(id: String): Flow<DataState<Customers>> = flow {
        emit(DataState.Loading)
        //try {
        val data = firestoreDB.collection("Customers").document(id)
            .get().await()
        if (data.exists())
            emit(DataState.Success(data.toObject(Customers::class.java)) as DataState<Customers>)
        else
            emit(DataState.Error<Any>(Exception("Invalid customer id")) as DataState<Customers>)
//        } catch (e: Exception) {
//            emit(DataState.Error<Any>(e) as DataState<Customers>)
//        }
    }.catch {
        emit(DataState.Error<Any>(Exception("it")) as DataState<Customers>)

    }

    suspend fun addCustomer(customer: Customers): Flow<DataState<Customers>> = flow {
        emit(DataState.Loading)
        try {
            firestoreDB.collection("Customers").document(customer.id!!)
                .set(customer).await()
            emit(DataState.Success(customer))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Customers>)
        }
    }

    suspend fun updateCustomer(customer: Customers): Flow<DataState<Customers>> = flow {
        emit(DataState.Loading)
        try {
            firestoreDB.collection("Customers").document(customer.id!!).set(
                customer,
                SetOptions.merge()
            ).await()
            emit(DataState.Success(customer))
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Customers>)
        }
    }
}