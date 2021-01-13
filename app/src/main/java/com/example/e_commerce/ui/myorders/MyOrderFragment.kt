package com.example.e_commerce.ui.myorders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentMyOrderBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
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
class MyOrderFragment :
    BaseFragment<FragmentMyOrderBinding, MyOrderViewModel>(false) {
    private val homeViewModel: MyOrderViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var orders: MutableList<Orders> = mutableListOf()
    private lateinit var progress: CustomProgressDialogue
    private val parentList: HashMap<Orders, HashMap<Products, OrderDetails>> = hashMapOf()
    private lateinit var adapter: MyOrderAdapter
    private var iterate = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onClick()
        getAllOrders()
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

    private fun getAllOrders() {
        lifecycleScope.launch {
            getViewModel().getAllOrders()
        }
    }

    private fun getOrdersDetails(categories: List<Categories>) {
        for (category in categories) {
            getOrdersDetails(orders, category)
        }

    }

    private fun getOrdersDetails(orders: List<Orders>, category: Categories) {
        lifecycleScope.launch {
            getViewModel().getOrdersDetails(orders, category)
        }
    }


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        progress = CustomProgressDialogue(requireContext())
        adapter = MyOrderAdapter(orders, hashMapOf(), clickListener())
        getViewDataBinding().rvOrders.adapter = adapter
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->

    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        getViewModel().dataStateCategories.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<List<Categories>> -> {
                    categories = it.data.toMutableList()
                    getOrdersDetails(categories)
                }
                is DataState.Error<*> -> {

                }
            }

        })

        getViewModel().dataStateOrders.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                    getViewDataBinding().isLoading = true
                }
                is DataState.Success<List<Orders>> -> {
                    orders = it.data.sortedBy { it.delevereded }.toMutableList()
                    adapter.addItems(orders)
                    getCategoriesData()
                }
                is DataState.Error<*> -> {

                }
            }
        })

        getViewModel().dataStateOrdersDetails.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<HashMap<Orders, HashMap<Products, OrderDetails>>> -> {
                    it.data.onEach {
                        if (parentList.containsKey(it.key)) {
                            parentList[it.key]!! += it.value
                        } else
                            parentList[it.key] = it.value
                    }
                    iterate++
                    if (iterate == 3) {
                        parentList.values.onEach {
                            Log.d("rerrererrooo::", it.values.map { it.orderId }.toString())

                        }
                        adapter.addOrderDetails(parentList)
                        progress.dismiss()
                        getViewDataBinding().isLoading = false
                    }

                }
                is DataState.Error<*> -> {

                }
            }
        })
    }

    override
    val layoutId: Int
        get() = R.layout.fragment_my_order

    override
    val bindingVariableId: Int
        get() = 0

    override
    val bindingVariableValue: Any
        get() = getViewModel()


    private fun onClick() {
        getViewDataBinding().byButton.setOnClickListener {
        }

    }


}