package com.example.e_commerce.ui.search

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.budiyev.android.codescanner.*
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentSearchBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.utils.CustomProgressDialogue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(false), SearchView.OnQueryTextListener {
    private val homeViewModel: SearchViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    private lateinit var adapter: SearchAdapter
    private lateinit var codeScanner: CodeScanner
    private lateinit var progress: CustomProgressDialogue
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onClick()
        getCategoriesData()
        observeData()
        setViews()
        return getMRootView()
    }


    override fun getViewModel() = homeViewModel

    private fun getCategoriesData() {
        lifecycleScope.launch {
            getViewModel().getAllCategories()
        }
    }

    private fun getAllShoppingProducts(categories: List<Categories>) {
        for (category in categories) {
            getProductsByCategory(category)
        }

    }

    private fun getProductsByCategory(category: Categories) {
        lifecycleScope.launch {
            getViewModel().getProductsByCategory(category)
        }
    }

    private fun addToShoppingCart(product: Products) {
        lifecycleScope.launch {
            getViewModel().addToShoppingCart(product)
        }
    }

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        progress = CustomProgressDialogue(requireContext())
        codeScanner = CodeScanner(requireContext(), getViewDataBinding().scannerView)
        adapter = SearchAdapter(products, products, clickListener())
        getViewDataBinding().rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        getViewDataBinding().rvProducts.adapter = adapter

    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->
        if (view.id == R.id.add_button) {
            if (adapter.listView[position]!!.isFav != true) {
                adapter.listView[position]!!.isFav = true
                addToShoppingCart(adapter.getItem(position))
            }
        } else {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(
                    adapter.getItem(position)
                )
            )
        }
    }

    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }
                is DataState.Success<List<Categories>> -> {
                    categories = it.data.toMutableList()
                    getAllShoppingProducts(categories)
                }
                is DataState.Error<*> -> {

                }
            }
        })

        getViewModel().dataStateProducts.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }
                is DataState.Success<List<Products>> -> {
                    progress.dismiss()
                    adapter.itemsSearchProducts = it.data.toMutableList()
                }
                is DataState.Error<*> -> {

                }
            }

        })

    }

    override val layoutId: Int
        get() = R.layout.fragment_search
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()

    private fun checkPermissionVoice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    private fun checkPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        }
    }

    private fun scannerNow() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissionCamera()
        }
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                getViewDataBinding().scannerView.visibility = View.GONE
                getViewDataBinding().voiceSearch.visibility = View.VISIBLE
                getViewDataBinding().rvProducts.visibility = View.VISIBLE
                getViewDataBinding().btnSearch.visibility = View.VISIBLE
                adapter.filter.filter(it.text)
                codeScanner.stopPreview()
                Toast.makeText(requireContext(), "Scan result: ${it.text}", Toast.LENGTH_LONG)
                    .show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        codeScanner.startPreview()
    }

    private fun onClick() {
        getViewDataBinding().btnSearch.setOnQueryTextListener(this)
        getViewDataBinding().voiceSearch.setOnClickListener {
            voiceNow()
        }
        getViewDataBinding().qrSearch.setOnClickListener {
            getViewDataBinding().scannerView.visibility = View.VISIBLE
            getViewDataBinding().linearLayout.visibility = View.GONE
            getViewDataBinding().linearLayout2.visibility = View.GONE
            getViewDataBinding().textView.visibility = View.GONE
            getViewDataBinding().rvProducts.visibility = View.GONE
            getViewDataBinding().btnSearch.visibility = View.GONE
            scannerNow()

        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        getViewDataBinding().searching = true
        adapter.filter.filter(query)

        return false
    }

    private fun voiceNow() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissionVoice()
        }

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something")
        try {
            Log.d("orderrrreeee", "requestCode.toString()")
            startActivityForResult(speechRecognizerIntent, 1)
        } catch (e: Exception) {
            Log.d("orderrrreeee", e.message!!)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        getViewDataBinding().searching = true
//        if (adapter.getItems().isEmpty()) {
//            getViewDataBinding().searching = false
//        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("orderrrreeee", requestCode.toString())
        when (requestCode) {
            1 -> if (resultCode === RESULT_OK && data != null) {
                val result: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                Log.d("orderrrreeee", result[0])
                getViewDataBinding().btnSearch.setQuery("", false)
                getViewDataBinding().btnSearch.clearFocus()
                adapter.filter.filter(result[0])
                getViewDataBinding().searching = true

            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun getBackPressed(): Boolean {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToHomeFragment())
        return true
    }
}