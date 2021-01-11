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
import com.example.e_commerce.databinding.FragmentResetPasswordBinding
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding, ResetPasswordViewModel>(false) {
    private val homeViewModel: ResetPasswordViewModel by viewModels()
    var id: String = ""
    private lateinit var customer: Customers
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

    private fun upDateCustomer(customers: Customers) {
        lifecycleScope.launch {
            getViewModel().upDateCustomer(customers)
        }
    }

    private fun updatePassword(password: String) {
        lifecycleScope.launch {
            getViewModel().updatePassword(password)
        }
    }


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this

    }

    private fun observeData() {
        getViewModel().dataStateGetCustomers.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Customers> -> {
                    customer = it.data
                    signIn(
                        it.data.userName!!,
                        it.data.password!!
                    )
                    customer.password = getViewDataBinding().etEnterPassword.text.toString()
                }
                is DataState.Error<*> -> {
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        getViewModel().dataStateSignIn.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    updatePassword(
                        getViewDataBinding().etEnterPassword.text.toString()
                    )
                }
                is DataState.Error<*> -> {
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        getViewModel().dataStateUpdatePassword.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    upDateCustomer(customer)
                }
                is DataState.Error<*> -> {
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })

        getViewModel().dataStateUpdateCustomers.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Customers> -> {
                    findNavController().popBackStack()
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
        get() = R.layout.fragment_reset_password
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().btnConfirmPassword.setOnClickListener {
            getCustomer(getViewDataBinding().etEnteremail.text.toString())
        }
    }


}