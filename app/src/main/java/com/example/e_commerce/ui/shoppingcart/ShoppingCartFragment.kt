package com.example.e_commerce.ui.shoppingcart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentShoppingCartBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.ui.base.ItemLongClickListener
import com.example.e_commerce.ui.base.RecyclerTouchListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShoppingCartFragment :
    BaseFragment<FragmentShoppingCartBinding, ShoppingCartViewModel>(true) {
    private val homeViewModel: ShoppingCartViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    lateinit var builder1: AlertDialog.Builder
    private var ordersDetails: MutableList<OrderDetails> = mutableListOf()
    var clickedPosition: Int = 0
    private lateinit var adapter: ShoppingCartAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        builder1 = AlertDialog.Builder(requireContext())
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

    private fun removeFromShoppingCart(product: Products, orderDetails: OrderDetails) {
        lifecycleScope.launch {
            getViewModel().removeFromShoppingCart(product, orderDetails)
        }
    }

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        builder1.setTitle("delete product")
        builder1.setMessage("are you want to delete this product from Shopping cart!!")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            removeFromShoppingCart(
                adapter.getItem(clickedPosition),
                adapter.itemsOrderDetails[clickedPosition]
            )
            adapter.removeItem(clickedPosition)
            adapter.removeOrderDetails(clickedPosition)
            dialog.cancel()
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id ->
            dialog.cancel()
        }
        val alert11: AlertDialog = builder1.create()
        adapter = ShoppingCartAdapter(products, ordersDetails, clickListener())
        getViewDataBinding().rvProducts.adapter = adapter
        getViewDataBinding().rvProducts.addOnItemTouchListener(
            RecyclerTouchListener(requireContext(),
                getViewDataBinding().rvProducts, object : ItemLongClickListener {
                    override fun onLongClick(position: Int, view: View) {
                        clickedPosition = position
                        alert11.show()
                    }
                })
        )
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->
        clickedPosition = position
        if (view.id == R.id.remove_item) {
            decrementQuantity(adapter.getItem(position))
        } else {
            incrementQuantity(adapter.getItem(position))
        }
    }

    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            categories = it.toMutableList()
            getAllShoppingProducts(categories)
        })

        getViewModel().dataStateProducts.observe(viewLifecycleOwner, {
            ordersDetails = it.map { it.value }.toMutableList()
            products = it.map { it.key }.toMutableList()
            adapter.addItems(products)
            adapter.addOrderDetails(ordersDetails)
        })

        getViewModel().dataStateIncrementProducts.observe(viewLifecycleOwner, {
            if (it) {
                val product = adapter.getItem(clickedPosition)
                product.quantity--
                adapter.listView[clickedPosition]!!.product = product
                val orderDetails = adapter.itemsOrderDetails[clickedPosition]
                orderDetails.quantity++
                adapter.listView[clickedPosition]!!.orderDetails = orderDetails
            }
        })

        getViewModel().dataStateDecrementProducts.observe(viewLifecycleOwner, {
            if (it) {
                val product = adapter.getItem(clickedPosition)
                product.quantity++
                adapter.listView[clickedPosition]!!.product = product
                val orderDetails = adapter.itemsOrderDetails[clickedPosition]
                orderDetails.quantity--
                adapter.listView[clickedPosition]!!.orderDetails = orderDetails
            }
        })
    }

    override val layoutId: Int
        get() = R.layout.fragment_shopping_cart
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().byButton.setOnClickListener {
            findNavController().navigate(ShoppingCartFragmentDirections.actionShoppingCartFragmentToReviewOrderFragment())
        }

    }


}