package com.example.e_commerce.ui.shoppingcart

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.movie.domain.state.DataState
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
    private val _dataStateIncrementProducts: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateIncrementProducts: LiveData<DataState<Boolean>>
        get() = _dataStateIncrementProducts
    private val _dataStateDecrementProducts: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateDecrementProducts: MutableLiveData<DataState<Boolean>>
        get() = _dataStateDecrementProducts
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

    suspend fun removeFromShoppingCart(product: Products, orderDetails: OrderDetails) {
        viewModelScope.launch {
            fireBaseService.removeFromShoppingCart(product, orderDetails).onEach {

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

}