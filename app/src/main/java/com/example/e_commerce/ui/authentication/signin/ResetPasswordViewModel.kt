package com.example.e_commerce.ui.authentication.signin

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.movie.domain.state.DataState
import com.example.e_commerce.datasource.dbservice.AuthenticationRepository
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Customers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ResetPasswordViewModel
@ViewModelInject
constructor(
    private val authentication: AuthenticationRepository,
    private val fireBaseService: FireBaseRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataStateGetCustomers: MutableLiveData<DataState<Customers>> =
        MutableLiveData()
    val dataStateGetCustomers: LiveData<DataState<Customers>>
        get() = _dataStateGetCustomers

    private val _dataStateUpdateCustomers: MutableLiveData<DataState<Customers>> =
        MutableLiveData()
    val dataStateUpdateCustomers: LiveData<DataState<Customers>>
        get() = _dataStateUpdateCustomers

    private val _dataStateSignIn: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateSignIn: LiveData<DataState<Boolean>>
        get() = _dataStateSignIn

    private val _dataStateUpdatePassword: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateUpdatePassword: LiveData<DataState<Boolean>>
        get() = _dataStateUpdatePassword

    suspend fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authentication.signIn(email, password).onEach {
                _dataStateSignIn.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun getCustomer(id: String) {
        viewModelScope.launch {
            fireBaseService.getCustomer(id).onEach {
                _dataStateGetCustomers.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun upDateCustomer(customers: Customers) {
        viewModelScope.launch {
            fireBaseService.updateCustomer(customers).onEach {
                _dataStateUpdateCustomers.value = it
            }.launchIn(viewModelScope)
        }
    }

    suspend fun updatePassword(password: String) {
        viewModelScope.launch {
            authentication.updatePassword(password).onEach {
                _dataStateUpdatePassword.value = it
            }.launchIn(viewModelScope)
        }
    }
}