package com.example.e_commerce.ui.revieworder

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.movie.domain.state.DataState
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
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
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateOrder: MutableLiveData<DataState<Orders>> =
        MutableLiveData()
    val dataStateOrder: LiveData<DataState<Orders>>
        get() = _dataStateOrder

    private val _dataStateUpdateOrder: MutableLiveData<DataState<Orders>> =
        MutableLiveData()
    val dataStateUpdateOrder: LiveData<DataState<Orders>>
        get() = _dataStateUpdateOrder

    private val _dataStateProducts: MutableLiveData<DataState<HashMap<Products, OrderDetails>>> =
        MutableLiveData()
    val dataStateProducts: MutableLiveData<DataState<HashMap<Products, OrderDetails>>>
        get() = _dataStateProducts
    private val _dataStateCategories: MutableLiveData<DataState<List<Categories>>> =
        MutableLiveData()
    val dataStateCategories: LiveData<DataState<List<Categories>>>
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
            fireBaseService.getCurrentOrder().onEach {
                _dataStateOrder.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun updateOrder(orders: Orders) {
        viewModelScope.launch {
            fireBaseService.updateOrder(orders).onEach {
                _dataStateUpdateOrder.value = it
            }.launchIn(viewModelScope)
        }
    }

}