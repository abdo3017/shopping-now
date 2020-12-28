package com.example.e_commerce.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentSearchBinding
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
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(), SearchView.OnQueryTextListener {
    private val homeViewModel: SearchViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    private lateinit var adapter: SearchAdapter
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
        adapter = SearchAdapter(products, products, clickListener())
        getViewDataBinding().rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
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
            getAllShoppingProducts(categories)
        })

        getViewModel().dataStateProducts.observe(viewLifecycleOwner, {
            adapter.itemsSearchProducts = it.toMutableList()
        })

    }

    override val layoutId: Int
        get() = R.layout.fragment_search
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().btnSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.filter.filter(query)
        if (adapter.getItems().isEmpty()) {
            getViewDataBinding().searching = false
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        getViewDataBinding().searching = true
        if (adapter.getItems().isEmpty()) {
            getViewDataBinding().searching = false
        }
        return false
    }


}