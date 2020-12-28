package com.example.e_commerce.ui.authentication.signin

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
class SignInViewModel
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

    private val _dataStateSignIn: MutableLiveData<Boolean> =
        MutableLiveData()
    val dataStateSignIn: LiveData<Boolean>
        get() = _dataStateSignIn

    suspend fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _dataStateSignIn.value = authentication.signIn(email, password)
        }
    }

    suspend fun getCustomer(id: String) {
        viewModelScope.launch {
            _dataStateCustomers.value = fireBaseService.getCustomer(id)!!
        }
    }


}