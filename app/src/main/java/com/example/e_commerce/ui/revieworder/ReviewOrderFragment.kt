package com.example.e_commerce.ui.revieworder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.movie.domain.state.DataState
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentReviewOrderBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.utils.CustomProgressDialogue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReviewOrderFragment :
    BaseFragment<FragmentReviewOrderBinding, ReviewOrderViewModel>(false) {
    private val homeViewModel: ReviewOrderViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    var price: Double = 0.0
    private lateinit var progress: CustomProgressDialogue
    private var ordersDetails: MutableList<OrderDetails> = mutableListOf()
    var lastProducts: Int = 0
    private lateinit var adapter: ReviewOrderAdapter

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
            getProductsShoppingCartDataByCategory(category)
        }

    }

    private fun getProductsShoppingCartDataByCategory(category: Categories) {
        lifecycleScope.launch {
            getViewModel().getProductsShoppingCartByCategory(category)
        }
    }


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        progress = CustomProgressDialogue(requireContext())
        adapter = ReviewOrderAdapter(products, ordersDetails, clickListener())
        getViewDataBinding().rvProducts.adapter = adapter
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->

    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                    getViewDataBinding().isLoading = true
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

                }
                is DataState.Success<HashMap<Products, OrderDetails>> -> {
                    lastProducts++
                    ordersDetails = it.data.map { it.value }.toMutableList()
                    products = it.data.map { it.key }.toMutableList()
                    price += products.zip(ordersDetails).toMap()
                        .map { it.key.price * it.value.quantity }
                        .sum()
                    if (lastProducts == 3)
                        progress.dismiss()
                    getViewDataBinding().isLoading = true
                    getViewDataBinding().byButton.text =
                        getViewDataBinding().byButton.text.toString() + " / $price"
                    adapter.addItems(products)
                    adapter.addOrderDetails(ordersDetails)
                    adapter.addItems(products)
                    adapter.addOrderDetails(ordersDetails)
                }
                is DataState.Error<*> -> {

                }
            }
        })
    }

    override
    val layoutId: Int
        get() = R.layout.fragment_review_order

    override
    val bindingVariableId: Int
        get() = 0

    override
    val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().byButton.setOnClickListener {
            findNavController().navigate(ReviewOrderFragmentDirections.actionReviewOrderFragmentToMapFragment())
        }

    }


}