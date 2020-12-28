package com.example.e_commerce.ui.authentication.signup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.AuthenticationService
import com.example.e_commerce.datasource.dbservice.FireBaseService
import com.example.e_commerce.datasource.models.Customers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class RegisterViewModel
@ViewModelInject
constructor(
    private val authentication: AuthenticationService,
    private val fireBaseService: FireBaseService,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataStateCustomers: MutableLiveData<Customers> =
        MutableLiveData()
    val dataStateCustomers: LiveData<Customers>
        get() = _dataStateCustomers

    private val _dataStateSignUp: MutableLiveData<Boolean> =
        MutableLiveData()
    val dataStateSignUp: LiveData<Boolean>
        get() = _dataStateSignUp

    suspend fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _dataStateSignUp.value = authentication.signUp(email, password)
        }
    }

    suspend fun addCustomer(customer: Customers) {
        viewModelScope.launch {
            fireBaseService.addCustomer(customer)
        }
    }


}