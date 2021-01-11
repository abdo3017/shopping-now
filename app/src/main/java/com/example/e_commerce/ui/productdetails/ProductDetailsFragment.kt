package com.example.e_commerce.ui.productdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce.BR
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
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
class ProductDetailsFragment :
    BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>(false) {
    private val homeViewModel: ProductDetailsViewModel by viewModels()
    private lateinit var orders: Orders
    var quantity = 0
    private lateinit var orderDetails: OrderDetails
    private lateinit var progress: CustomProgressDialogue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onClick()
        getViewDataBinding().product =
            ProductDetailsFragmentArgs.fromBundle(requireArguments()).product
        getCurrentOrder()
        observeData()
        setViews()
        return getMRootView()
    }

    override fun getViewModel() = homeViewModel

    private fun getCurrentOrder() {
        lifecycleScope.launch {
            getViewModel().getCurrentOrder()
        }
    }

    private fun getOrderDetails(productId: String, orderId: String) {
        lifecycleScope.launch {
            getViewModel().getOrderDetails(productId, orderId)
        }
    }

    private fun incrementQuantity(product: Products) {
        lifecycleScope.launch {
            getViewModel().incrementQuantity(product)
        }
    }

    private fun decrementQuantity(product: Products) {
        lifecycleScope.launch {
            getViewModel().decrementQuantity(product)
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

    }

    private fun observeData() {
        getViewModel().dataStateOrder.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }
                is DataState.Success<Orders> -> {
                    orders = it.data
                    getOrderDetails(getViewDataBinding().product!!.id!!, it.data.id!!)
                }
                is DataState.Error<*> -> {

                }
            }
        })

        getViewModel().dataStateOrderDetails.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<OrderDetails> -> {


                    getViewDataBinding().isLoading = true
                    addedToCart()
                    progress.dismiss()
                    orderDetails = it.data
                    Log.d("rerererer", orderDetails.toString())

                }
                is DataState.Error<*> -> {
                    Log.d("rerererer", "orderDetails.toString()")
                    getViewDataBinding().isLoading = true
                    progress.dismiss()
                }
            }
        })

        getViewModel().dataStateIncrementProducts.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    if (it.data) {
                        val product = getViewDataBinding().product
                        product!!.quantity--
                        getViewDataBinding().product = product
                        orderDetails.quantity++
                    }
                }
                is DataState.Error<*> -> {

                }
            }

        })

        getViewModel().dataStateDecrementProducts.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    if (it.data) {
                        val product = getViewDataBinding().product
                        product!!.quantity++
                        getViewDataBinding().product = product
                        orderDetails.quantity--
                    }

                }
                is DataState.Error<*> -> {

                }
            }
        })
    }

    override val layoutId: Int
        get() = R.layout.fragment_product_details
    override val bindingVariableId: Int
        get() = BR.product
    override val bindingVariableValue: Any
        get() = ProductDetailsFragmentArgs.fromBundle(requireArguments()).product


    private fun onClick() {
        getViewDataBinding().addItem.setOnClickListener {
            quantity++
            getViewDataBinding().iteamAmount.text = quantity.toString()
            incrementQuantity(getViewDataBinding().product!!)
        }
        getViewDataBinding().removeItem.setOnClickListener {
            quantity--
            getViewDataBinding().iteamAmount.text = quantity.toString()
            decrementQuantity(getViewDataBinding().product!!)
        }
        getViewDataBinding().byButton.setOnClickListener {
            if (quantity == 0) {
                addedToCart()
                addToShoppingCart(getViewDataBinding().product!!)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addedToCart() {
        quantity++
        getViewDataBinding().showAdd = true
        getViewDataBinding().byButton.text = "added to cart"
        getViewDataBinding().byButton.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_added,
            0
        )
    }
}
