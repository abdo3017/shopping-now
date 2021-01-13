package com.example.e_commerce.ui.myorders

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Categories
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
class MyOrderViewModel
@ViewModelInject
constructor(
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateOrders: MutableLiveData<DataState<List<Orders>>> =
        MutableLiveData()
    val dataStateOrders: LiveData<DataState<List<Orders>>>
        get() = _dataStateOrders

    private val _dataStateOrdersDetails: MutableLiveData<DataState<HashMap<Orders, HashMap<Products, OrderDetails>>>> =
        MutableLiveData()
    val dataStateOrdersDetails: MutableLiveData<DataState<HashMap<Orders, HashMap<Products, OrderDetails>>>>
        get() = _dataStateOrdersDetails
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

    suspend fun getOrdersDetails(orders: List<Orders>, category: Categories) {
        viewModelScope.launch {
            fireBaseService.getOrdersDetails(orders, category).onEach {
                _dataStateOrdersDetails.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun getAllOrders() {
        viewModelScope.launch {
            fireBaseService.getAllOrders().onEach {
                _dataStateOrders.value = it
            }.launchIn(viewModelScope)
        }
    }


}