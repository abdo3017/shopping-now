package com.example.e_commerce.ui.authentication.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, SignInViewModel>(false) {
    private val homeViewModel: SignInViewModel by viewModels()
    var id: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onClick()
        observeData()
        setViews()
        return getMRootView()
    }


    override fun getViewModel() = homeViewModel

    private fun signIn(email: String, password: String) {
        id = email
        lifecycleScope.launch {
            getViewModel().signIn(email, password)
        }
    }

    private fun getCustomer(id: String) {
        lifecycleScope.launch {
            getViewModel().getCustomer(id)
        }
    }


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
    }

    private fun observeData() {
        getViewModel().dataStateSignIn.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    getCustomer(id)
                }
                is DataState.Error<*> -> {
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })

        getViewModel().dataStateGetCustomers.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Customers> -> {
                    PrefManager.saveCustomer(it.data)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
                is DataState.Error<*> -> {
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override val layoutId: Int
        get() = R.layout.fragment_login
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()

    private fun onClick() {
        getViewDataBinding().btnLogin.setOnClickListener {
            if (getViewDataBinding().checkRemember.isChecked) {
                PrefManager.saveRememberMe("remember")
            }
            signIn(
                getViewDataBinding().etEnterEmail.text.toString(),
                getViewDataBinding().etEnterPasswordLogin.text.toString()
            )
        }
        getViewDataBinding().tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
        getViewDataBinding().tvForgetPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
        }
    }
}