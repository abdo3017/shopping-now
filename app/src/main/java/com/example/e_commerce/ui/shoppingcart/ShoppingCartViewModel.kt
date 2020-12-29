package com.example.e_commerce.ui.shoppingcart

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ShoppingCartViewModel
@ViewModelInject
constructor(
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataStateIncrementProducts: MutableLiveData<Boolean> =
        MutableLiveData()
    val dataStateIncrementProducts: LiveData<Boolean>
        get() = _dataStateIncrementProducts
    private val _dataStateDecrementProducts: MutableLiveData<Boolean> =
        MutableLiveData()
    val dataStateDecrementProducts: MutableLiveData<Boolean>
        get() = _dataStateDecrementProducts
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

    suspend fun removeFromShoppingCart(product: Products, orderDetails: OrderDetails) {
        viewModelScope.launch {
            fireBaseService.removeFromShoppingCart(product, orderDetails)
        }
    }

    suspend fun incrementQuantity(product: Products) {
        viewModelScope.launch {
            _dataStateIncrementProducts.value = fireBaseService.incrementQuantity(product)
        }
    }

    suspend fun decrementQuantity(product: Products) {
        viewModelScope.launch {
            _dataStateDecrementProducts.value = fireBaseService.decrementQuantity(product)
        }
    }

}