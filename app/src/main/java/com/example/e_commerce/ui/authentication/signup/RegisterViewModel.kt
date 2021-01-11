package com.example.e_commerce.ui.authentication.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.AuthenticationRepository
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.state.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class RegisterViewModel
@ViewModelInject
constructor(
    private val authentication: AuthenticationRepository,
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataStateCustomers: MutableLiveData<DataState<Customers>> =
        MutableLiveData()
    val dataStateCustomers: LiveData<DataState<Customers>>
        get() = _dataStateCustomers

    private val _dataStateSignUp: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateSignUp: LiveData<DataState<Boolean>>
        get() = _dataStateSignUp

    suspend fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authentication.signUp(email, password).onEach {
                _dataStateSignUp.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun addCustomer(customer: Customers) {
        viewModelScope.launch {
            fireBaseService.addCustomer(customer).onEach {
                _dataStateCustomers.value = it
            }.launchIn(viewModelScope)
        }
    }


}