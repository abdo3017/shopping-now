package com.example.e_commerce.ui.authentication.resetpassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.e_commerce.utils.CustomProgressDialogue
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
    private lateinit var progress: CustomProgressDialogue
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
        progress = CustomProgressDialogue(requireContext())
    }

    private fun observeData() {
        getViewModel().dataStateGetCustomers.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
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
                    progress.dismiss()
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

    private fun checkValidation(): Boolean {
        var check = true
        if (getViewDataBinding().etEnteremail.text.isNullOrEmpty()) {
            getViewDataBinding().tilEmail.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterPassword.text.isNullOrEmpty()) {
            getViewDataBinding().tilPassword.error = "empty"
            check = false
        }

        return check
    }

    private fun setUpViewsChanges() {
        getViewDataBinding().etEnteremail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilEmail.isErrorEnabled) {
                    getViewDataBinding().tilEmail.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        getViewDataBinding().etEnterPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilPassword.isErrorEnabled) {
                    getViewDataBinding().tilPassword.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    override fun getBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}