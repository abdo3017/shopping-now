package com.example.e_commerce.ui.authentication.signin

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
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.CustomProgressDialogue
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
    private lateinit var progress: CustomProgressDialogue
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
        progress = CustomProgressDialogue(requireContext())
    }

    private fun observeData() {
        getViewModel().dataStateSignIn.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
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
                    progress.dismiss()
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
            if (checkValidation()) {
                if (getViewDataBinding().checkRemember.isChecked) {
                    PrefManager.saveRememberMe("remember")
                }
                signIn(
                    getViewDataBinding().etEnterEmail.text.toString(),
                    getViewDataBinding().etEnterPasswordLogin.text.toString()
                )
            }
        }
        getViewDataBinding().tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
        getViewDataBinding().tvForgetPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
        }
    }

    private fun checkValidation(): Boolean {
        var check = true
        if (getViewDataBinding().etEnterEmail.text.isNullOrEmpty()) {
            getViewDataBinding().tilUserName.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterPasswordLogin.text.isNullOrEmpty()) {
            getViewDataBinding().tilPassword.error = "empty"
            check = false
        }
        return check
    }

    private fun setUpViewsChanges() {
        getViewDataBinding().etEnterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilUserName.isErrorEnabled) {
                    getViewDataBinding().tilUserName.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        getViewDataBinding().etEnterPasswordLogin.addTextChangedListener(object : TextWatcher {
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
}