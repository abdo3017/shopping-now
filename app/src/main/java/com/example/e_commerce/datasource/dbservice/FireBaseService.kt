package com.example.e_commerce.datasource.dbservice

import android.content.ContentValues.TAG
import android.util.Log
import com.example.e_commerce.datasource.models.*
import com.example.e_commerce.utils.PrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FireBaseService
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

    suspend fun getAllCategories(): Flow<List<Categories>> {
        return callbackFlow {
            val listenerRegistration = firestoreDB.collection("Categories")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(
                            message = "Error fetching Categories",
                            cause = firebaseFirestoreException
                        )
                        return@addSnapshotListener
                    }
                    val map = querySnapshot!!.documents.mapNotNull {
                        it.toObject(Categories::class.java)
                    }
                    offer(map)
                }
            awaitClose {
                Log.d(TAG, "Cancelling Categories listener")
                listenerRegistration.remove()
            }
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

    suspend fun updateProduct(product: Products) {
        Log.w(TAG, "updateProduct :${product.quantity}")
        firestoreDB.collection("Categories").document(product.catId!!)
            .collection("Products").document(product.id!!).set(
                product,
                SetOptions.merge()
            ).await()
    }


    suspend fun getProductsByCategory(category: Categories): Flow<List<Products>> {
        return callbackFlow {
            val order = getCurrentOrder()
            Log.d(TAG, "orderrrr:${PrefManager.getCustomer()}")
            var list: MutableList<Products>? = mutableListOf()
            val docs = firestoreDB.collection("Categories").document(category.id!!)
                .collection("Products").get().await().documents
            if (order != null) {
                for (document in docs) {
                    val product = document.toObject(Products::class.java)
                    val orderDetails = getOrderDetails(product!!.id!!, order.id!!)
                    Log.d(TAG, "product:${product}")
                    if (orderDetails == null) {
                        if (product.quantity > 0)
                            list!!.add(product)
                        //Log.d("ggggggg", "orderDetails:${orderDetails}")
                    }
                }
            } else {
                list = docs.mapNotNull {
                    it.toObject(Products::class.java)
                }.toMutableList()
            }
            offer(list!!.toList())
            awaitClose {
                Log.d(TAG, "Cancelling Categories listener")
            }
        }
    }

    suspend fun getProductsShoppingCartByCategory(category: Categories): Flow<HashMap<Products, OrderDetails>> {
        return callbackFlow {
            val order = getCurrentOrder()
            var list: HashMap<Products, OrderDetails>? = hashMapOf()
            val docs = firestoreDB.collection("Categories").document(category.id!!)
                .collection("Products").get().await().documents
            if (order != null) {
                for (document in docs) {
                    val product = document.toObject(Products::class.java)
                    val orderDetails = getOrderDetails(product!!.id!!, order.id!!)
                    if (orderDetails != null) {
                        //Log.d(TAG, "here fuck:${orderDetails}")
                        list!![product] = orderDetails
                        //list!!.add(product)
                        //Log.d("ggggggg", "orderDetails:${orderDetails}")
                    }
                }
            }
            //offer(list!!.toList())
            offer(list!!)
            awaitClose {
                Log.d(TAG, "Cancelling Categories listener")
            }
        }
    }

    suspend fun incrementQuantity(product: Products): Boolean? {
        if (product.quantity > 0) {
            product.quantity--
            updateProduct(product)
            val order = getCurrentOrder()
            val orderDetails = getOrderDetails(product.id!!, order!!.id!!)
            orderDetails!!.quantity++
            updateOrderDetails(orderDetails)
            return true
        }
        return false
    }

    suspend fun decrementQuantity(product: Products): Boolean? {
        val order = getCurrentOrder()
        Log.d(TAG, "decrementQuantity ${order}")
        val orderDetails = getOrderDetails(product.id!!, order!!.id!!)
        Log.d(TAG, "decrementQuantity ${orderDetails}")
        if (orderDetails!!.quantity > 1) {
            orderDetails.quantity--
            product.quantity++
            updateProduct(product)
            updateOrderDetails(orderDetails)
            return true
        }
        return false
    }


    suspend fun addToShoppingCart(product: Products) {
        val docs = firestoreDB.collection("Orders").whereEqualTo("submitted", false)
            .get().await().documents
        if (!docs.isNullOrEmpty() && docs[0].exists()) {
            //add in exist order
            Log.d(TAG, "add in exist order")
            addOrderDetails(
                OrderDetails(
                    docs[0].toObject(Orders::class.java)!!.id,
                    product.id,
                    0
                )
            )
        } else {
            //add in new order
            addOrUpdateOrder(
                Orders(
                    customerId = PrefManager.getCustomer()!!.id
                )
            )
            delay(2000)
            val order: Orders = getCurrentOrder()!!
            Log.d(TAG, "add in new order ${order}")
            addOrderDetails(
                OrderDetails(
                    order.id,
                    product.id,
                    0
                )
            )
        }
        incrementQuantity(product)
    }

    suspend fun removeFromShoppingCart(product: Products, orderDetails: OrderDetails) {
        val order = getCurrentOrder()
        //val orderDetails = getOrderDetails(product.id!!, order!!.id!!)
        product.quantity += orderDetails.quantity
        removeOrderDetails(orderDetails)
        updateProduct(product)
        if (checkExistOrder(order!!.id!!)!!)
            removeOrder(order)
    }

    fun addOrderDetails(orderDetails: OrderDetails) {

        firestoreDB.collection("OrderDetails")
            .document(orderDetails.orderId + "," + orderDetails.productId).set(orderDetails)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    suspend fun getOrderDetails(productId: String, orderId: String): OrderDetails? {
        kotlin.runCatching {
            return firestoreDB.collection("OrderDetails").document("$orderId,$productId")
                .get().await()!!.toObject(OrderDetails::class.java)
        }.getOrElse { return null }

    }

    suspend fun updateOrderDetails(orderDetails: OrderDetails) {
        firestoreDB.collection("OrderDetails")
            .document(orderDetails.orderId + "," + orderDetails.productId).set(
                orderDetails,
                SetOptions.merge()
            ).await()
    }

    suspend fun checkExistOrder(orderId: String): Boolean? {
        kotlin.runCatching {
            firestoreDB.collection("OrderDetails")
                .document(orderId).get().await()!!.toObject(Orders::class.java)
            return true
        }.getOrElse {
            return false
        }
    }

    fun removeOrderDetails(orderDetails: OrderDetails) {
        firestoreDB.collection("OrderDetails")
            .document(orderDetails.orderId + "," + orderDetails.productId).delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    suspend fun addOrUpdateOrder(order: Orders) {
        firestoreDB.collection("Orders").add(order)
            .addOnSuccessListener { documentReference ->
                order.id = documentReference.id
                firestoreDB.collection("Orders").document(documentReference.id).set(
                    order,
                    SetOptions.merge()
                )
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    suspend fun getCurrentOrder(): Orders? {
        val doc = firestoreDB.collection("Orders").whereEqualTo("submitted", false)
            .whereEqualTo("customerId", PrefManager.getCustomer()!!.id)
            .get().await().documents
        if (doc.isNullOrEmpty())
            return null
        return doc[0].toObject(Orders::class.java)

    }

    suspend fun updateOrder(orders: Orders) {
        firestoreDB.collection("Orders")
            .document(orders.id!!).set(
                orders,
                SetOptions.merge()
            ).await()
    }

    fun removeOrder(order: Orders) {
        firestoreDB.collection("Orders")
            .document(order.id!!).delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }


    suspend fun getCustomer(id: String): Customers? {
        kotlin.runCatching {
            return firestoreDB.collection("Customers").document(id)
                .get().await().toObject(Customers::class.java)
        }.getOrElse {
            return null
        }
    }

    suspend fun addCustomer(customer: Customers) {
        firestoreDB.collection("Customers").document(customer.id!!)
            .set(customer).await()

    }

    suspend fun updateCustomer(customer: Customers) {
        firestoreDB.collection("Customers").document(customer.id!!).set(
            customer,
            SetOptions.merge()
        ).await()
    }
}

//object DBFireBase {
//    fun getDB(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }
//}

