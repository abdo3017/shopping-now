package com.example.e_commerce.ui.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SearchViewModel
@ViewModelInject
constructor(
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataStateProducts: MutableLiveData<DataState<List<Products>>> =
        MutableLiveData()
    val dataStateProducts: LiveData<DataState<List<Products>>>
        get() = _dataStateProducts

    private val _dataStateAddToShoppingCart: MutableLiveData<DataState<Products>> =
        MutableLiveData()
    val dataStateAddToShoppingCart: LiveData<DataState<Products>>
        get() = _dataStateAddToShoppingCart

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

    suspend fun getProductsByCategory(category: Categories) {
        viewModelScope.launch {
            fireBaseService.getProductsByCategory(category).onEach {
                _dataStateProducts.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun addToShoppingCart(product: Products) {
        viewModelScope.launch {
            fireBaseService.addToShoppingCart(product).onEach {
                _dataStateAddToShoppingCart.value = it
            }
        }
    }

}