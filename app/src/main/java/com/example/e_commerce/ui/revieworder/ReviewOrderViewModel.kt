package com.example.e_commerce.ui.revieworder

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.FireBaseService
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ReviewOrderViewModel
@ViewModelInject
constructor(
    private val fireBaseService: FireBaseService,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateOrder: MutableLiveData<Orders> =
        MutableLiveData()
    val dataStateOrder: LiveData<Orders>
        get() = _dataStateOrder

    private val _dataStateProducts: MutableLiveData<HashMap<Products, OrderDetails>> =
        MutableLiveData()
    val dataStateProducts: MutableLiveData<HashMap<Products, OrderDetails>>
        get() = _dataStateProducts
    private val _dataStateCategories: MutableLiveData<List<Categories>> =
        MutableLiveData()
    val dataStateCategories: LiveData<List<Categories>>
        get() = _dataStateCategories

    suspend fun getAllCategories() {
        viewModelScope.launch {
            fireBaseService.getAllCategories().onEach {
                _dataStateCategories.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun getProductsShoppingCartByCategory(category: Categories) {
        viewModelScope.launch {
            fireBaseService.getProductsShoppingCartByCategory(category).onEach {
                _dataStateProducts.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun getCurrentOrder() {
        viewModelScope.launch {
            _dataStateOrder.value = fireBaseService.getCurrentOrder()
        }
    }

    suspend fun updateOrder(orders: Orders) {
        viewModelScope.launch {
            fireBaseService.updateOrder(orders)
        }
    }

}