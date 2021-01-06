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
class SignInViewModel
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

    private val _dataStateSignIn: MutableLiveData<DataState<Boolean>> =
        MutableLiveData()
    val dataStateSignIn: LiveData<DataState<Boolean>>
        get() = _dataStateSignIn

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


}