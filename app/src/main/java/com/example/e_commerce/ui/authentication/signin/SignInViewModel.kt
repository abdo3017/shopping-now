package com.example.e_commerce.ui.authentication.signin

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.e_commerce.datasource.dbservice.AuthenticationRepository
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Customers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SignInViewModel
@ViewModelInject
constructor(
    private val authentication: AuthenticationRepository,
    private val fireBaseService: FireBaseRepository,
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