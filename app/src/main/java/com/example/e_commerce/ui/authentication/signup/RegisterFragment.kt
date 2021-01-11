package com.example.e_commerce.ui.authentication.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(false),
    DatePickerDialog.OnDateSetListener {
    private val homeViewModel: RegisterViewModel by viewModels()
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

    private fun signUp(email: String, password: String) {
        lifecycleScope.launch {
            getViewModel().signUp(email, password)
        }
    }

    private fun addCustomer(customers: Customers) {
        lifecycleScope.launch {
            getViewModel().addCustomer(customers)
        }
    }


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this

    }


    private fun observeData() {
        getViewModel().dataStateSignUp.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    addCustomer(
                        getCustomerData()
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

        getViewModel().dataStateCustomers.observe(viewLifecycleOwner, {
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
        get() = R.layout.fragment_register
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()

    private fun showDatePickerDialog() = DatePickerDialog(
        requireContext(),
        this,
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ).show()

    private fun onClick() {
        getViewDataBinding().etEnterBirthDate.setOnClickListener {
            showDatePickerDialog()
        }
        getViewDataBinding().btnRegister.setOnClickListener {
            signUp(
                getViewDataBinding().etEnterEmail.text.toString(),
                getViewDataBinding().etEnterPassword.text.toString()
            )
        }
        getViewDataBinding().tvBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        getViewDataBinding().etEnterBirthDate.setText("$dayOfMonth - ${month + 1} - $year")
    }

    private fun getCustomerData() = Customers(
        id = getViewDataBinding().etEnterEmail.text.toString(),
        name = getViewDataBinding().etEnterName.text.toString(),
        userName = getViewDataBinding().etEnterEmail.text.toString(),
        password = getViewDataBinding().etEnterPassword.text.toString(),
        job = getViewDataBinding().etEnterJob.text.toString(),
        gender = if (getViewDataBinding().rbFemale.isChecked)
            "female"
        else
            "male",
        birthDate = getViewDataBinding().etEnterBirthDate.text.toString()
    )

}