package com.example.e_commerce.ui.productdetails

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.AuthenticationRepository
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ProductDetailsViewModel
@ViewModelInject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateAddToShoppingCart: MutableLiveData<DataState<Products>> =
        MutableLiveData()
    val dataStateAddToShoppingCart: LiveData<DataState<Products>>
        get() = _dataStateAddToShoppingCart

    private val _dataStateOrder: MutableLiveData<DataState<Orders>> =
        MutableLiveData()
    val dataStateOrder: LiveData<DataState<Orders>>
        get() = _dataStateOrder

    private val _dataStateIncrementProducts: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateIncrementProducts: LiveData<DataState<Boolean>>
        get() = _dataStateIncrementProducts
    private val _dataStateDecrementProducts: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateDecrementProducts: MutableLiveData<DataState<Boolean>>
        get() = _dataStateDecrementProducts
    private val _dataStateOrderDetails: MutableLiveData<DataState<OrderDetails>> =
        MutableLiveData()
    val dataStateOrderDetails: LiveData<DataState<OrderDetails>>
        get() = _dataStateOrderDetails

    suspend fun getOrderDetails(productId: String, orderId: String) {
        viewModelScope.launch {
            fireBaseService.getOrderDetails(productId, orderId).onEach {
                _dataStateOrderDetails.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun incrementQuantity(product: Products) {
        viewModelScope.launch {
            fireBaseService.incrementQuantity(product).onEach {
                _dataStateIncrementProducts.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun decrementQuantity(product: Products) {
        viewModelScope.launch {
            fireBaseService.decrementQuantity(product).onEach {
                _dataStateDecrementProducts.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun getCurrentOrder() {
        viewModelScope.launch {
            fireBaseService.getCurrentOrder().onEach {
                _dataStateOrder.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun addToShoppingCart(product: Products) {
        viewModelScope.launch {
            fireBaseService.addToShoppingCart(product).onEach {
                _dataStateAddToShoppingCart.value = it
            }.launchIn(viewModelScope)
        }
    }
}