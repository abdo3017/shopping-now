package com.example.e_commerce.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.datasource.dbservice.FireBaseService
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(true) {
    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var fireBaseService: FireBaseService
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    private lateinit var adapter: HomeAdapter
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
        fireBaseService = FireBaseService()
        lifecycleScope.launch {
            getViewModel().getAllCategories()
        }
    }

    private fun getProductsDataByCategory(category: Categories) {
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
        adapter = HomeAdapter(products, clickListener())
        getViewDataBinding().rvProducts.adapter = adapter
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->
        if (adapter.listView[position]!!.isFav != true) {
            adapter.listView[position]!!.isFav = true
            addToShoppingCart(adapter.getItem(position))
        }
    }

    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            categories = it.toMutableList()
            getProductsDataByCategory(categories[0])
        })

        getViewModel().dataStateProducts.observe(viewLifecycleOwner, {
            adapter.setItems(it)
            Log.d(ContentValues.TAG, "list: $it")

        })
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().tvClothes.setOnClickListener {
            getProductsDataByCategory(categories[0])
        }
        getViewDataBinding().tvElectronics.setOnClickListener {
            getProductsDataByCategory(categories[1])
            Log.d(ContentValues.TAG, "list: $products")
        }
        getViewDataBinding().tvFurniture.setOnClickListener {
            getProductsDataByCategory(categories[2])
        }
        getViewDataBinding().searchButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }

    }


}