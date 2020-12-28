package com.example.e_commerce.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.FireBaseService
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.Products
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeViewModel
@ViewModelInject
constructor(
    private val fireBaseService: FireBaseService,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateProducts: MutableLiveData<List<Products>> =
        MutableLiveData()
    val dataStateProducts: LiveData<List<Products>>
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

    suspend fun getProductsByCategory(category: Categories) {
        viewModelScope.launch {
            fireBaseService.getProductsByCategory(category).onEach {
                _dataStateProducts.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun addToShoppingCart(product: Products) {
        viewModelScope.launch {
            fireBaseService.addToShoppingCart(product)
        }
    }

}