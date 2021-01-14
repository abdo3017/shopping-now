package com.example.e_commerce.ui.authentication.signup

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
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
import com.example.e_commerce.utils.CustomProgressDialogue
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(false),
    DatePickerDialog.OnDateSetListener {
    private val homeViewModel: RegisterViewModel by viewModels()
    private lateinit var progress: CustomProgressDialogue
    private lateinit var imagePicker: ImagePicker.Builder
    private var upload: Boolean = false
    private lateinit var uri: Uri
    private var image: String? = null
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

    private fun uploadImage(uri: Uri, imageName: String) {
        lifecycleScope.launch {
            getViewModel().uploadImage(uri, imageName)
        }
    }

    private fun addCustomer(customers: Customers) {
        lifecycleScope.launch {
            getViewModel().addCustomer(customers)
        }
    }

    private fun getCustomer(customers: String) {
        lifecycleScope.launch {
            getViewModel().getCustomer(customers)
        }
    }

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        progress = CustomProgressDialogue(requireContext())
        imagePicker = ImagePicker.with(this)
            .crop()    //Crop image(Optional), Check Customization for more option
            .compress(1024)     //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)

        setUpViewsChanges()
    }


    private fun observeData() {
        getViewModel().dataStateGetCustomers.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }
                is DataState.Success<Customers> -> {
                    progress.dismiss()
                    getViewDataBinding().tilEmailPhone.error = "this email already used"
                }
                is DataState.Error<*> -> {
                    signUp(
                        getViewDataBinding().etEnterEmail.text.toString(),
                        getViewDataBinding().etEnterPassword.text.toString()
                    )
                }
            }
        })

        getViewModel().dataStateSignUp.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                }
                is DataState.Success<Boolean> -> {
                    if (upload) {
                        Log.d("rerrererrooo", getCustomerData().id + "." + "////" + uri)

                        uploadImage(uri, getCustomerData().id + "." + getMimeType(uri))
                    } else
                        addCustomer(
                            getCustomerData()
                        )
                }
                is DataState.Error<*> -> {
                    progress.dismiss()
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        getViewModel().dataStateUploadImage.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                }
                is DataState.Success<String> -> {
                    image = it.data
                    Log.d("erererrer", image.toString())
                    addCustomer(
                        getCustomerData()
                    )
                }
                is DataState.Error<*> -> {
                    progress.dismiss()
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
                    progress.dismiss()
                    findNavController().popBackStack()
                }
                is DataState.Error<*> -> {
                    progress.dismiss()
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
            if (checkValidation()) {
                getCustomer(getViewDataBinding().etEnterEmail.text.toString())
            }
        }
        getViewDataBinding().tvBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        getViewDataBinding().profileAddImage.setOnClickListener {
            imagePicker.start()
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
        birthDate = getViewDataBinding().etEnterBirthDate.text.toString(),
        image = image
    )

    private fun checkValidation(): Boolean {
        var check = true
        if (getViewDataBinding().etEnterEmail.text.isNullOrEmpty()) {
            getViewDataBinding().tilEmailPhone.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterBirthDate.text.isNullOrEmpty()) {
            getViewDataBinding().tilBirthDate.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterName.text.isNullOrEmpty()) {
            getViewDataBinding().tilName.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterPassword.text.isNullOrEmpty()) {
            getViewDataBinding().tilPassword.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterPassword2.text.isNullOrEmpty()) {
            getViewDataBinding().tilPassword2.error = "empty"
            check = false
        }
        if (getViewDataBinding().etEnterPassword.text!!.isNotEmpty() &&
            getViewDataBinding().etEnterPassword2.text!!.isNotEmpty() &&
            getViewDataBinding().etEnterPassword.text.toString() != getViewDataBinding().etEnterPassword2.text.toString()
        ) {
            getViewDataBinding().tilPassword2.error = "must equals"
            check = false
        }
        if (getViewDataBinding().etEnterJob.text.isNullOrEmpty()) {
            getViewDataBinding().tilJob.error = "empty"
            check = false
        }
        return check
    }

    private fun setUpViewsChanges() {
        getViewDataBinding().etEnterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilEmailPhone.isErrorEnabled) {
                    getViewDataBinding().tilEmailPhone.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        getViewDataBinding().etEnterBirthDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilBirthDate.isErrorEnabled) {
                    getViewDataBinding().tilBirthDate.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        getViewDataBinding().etEnterName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilName.isErrorEnabled) {
                    getViewDataBinding().tilName.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        getViewDataBinding().etEnterJob.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilJob.isErrorEnabled) {
                    getViewDataBinding().tilJob.isErrorEnabled = false
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
        getViewDataBinding().etEnterPassword2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (getViewDataBinding().tilPassword2.isErrorEnabled) {
                    getViewDataBinding().tilPassword2.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                uri = data?.data!!
                upload = true
                getViewDataBinding().profileImage.setImageURI(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // url = file path or whatever suitable URL you want.
    private fun getMimeType(file: Uri): String? {
        //Check uri format to avoid null
        return if (file.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(requireContext().contentResolver.getType(file))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(file.path!!)).toString())
        }
    }
}