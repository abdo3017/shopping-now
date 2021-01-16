package com.example.e_commerce.ui.shoppingcart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentShoppingCartBottomSheetBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShoppingCartBottomCartFragment :
    BaseBottomSheetFragment<FragmentShoppingCartBottomSheetBinding, ShoppingCartViewModel>() {
    private val homeViewModel: ShoppingCartViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    lateinit var showDialog: AlertDialog.Builder
    private var ordersDetails: MutableList<OrderDetails> = mutableListOf()
    var clickedPosition: Int = 0
    private lateinit var adapter: ShoppingCartAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        showDialog = AlertDialog.Builder(requireContext())
        onClick()

        setViews()
        return getMRootView()
    }

    override fun getViewModel() = homeViewModel

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        getViewDataBinding().totalPrice =
            ShoppingCartBottomCartFragmentArgs.fromBundle(requireArguments()).totalPrice
    }


    override val layoutId: Int
        get() = R.layout.fragment_shopping_cart_bottom_sheet
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().byButton.setOnClickListener {
            findNavController().navigate(R.id.reviewOrderFragment)
        }

    }


}