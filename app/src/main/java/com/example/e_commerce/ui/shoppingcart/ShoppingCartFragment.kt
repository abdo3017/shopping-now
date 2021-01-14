package com.example.e_commerce.ui.shoppingcart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentShoppingCartBinding
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.ui.base.ItemLongClickListener
import com.example.e_commerce.ui.base.RecyclerTouchListener
import com.example.e_commerce.utils.CustomProgressDialogue
import com.example.e_commerce.utils.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShoppingCartFragment :
    BaseFragment<FragmentShoppingCartBinding, ShoppingCartViewModel>(false) {
    private val homeViewModel: ShoppingCartViewModel by viewModels()
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    lateinit var showDialog: AlertDialog.Builder
    private var ordersDetails: MutableList<OrderDetails> = mutableListOf()
    var clickedPosition: Int = 0
    var cat3 = 0
    private lateinit var progress: CustomProgressDialogue
    private lateinit var adapter: ShoppingCartAdapter
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

    private lateinit var alert: AlertDialog
    private fun setValidDialog() {
        showDialog.setTitle("delete product")
        showDialog.setMessage("are you want to delete this product from Shopping cart!!")
        showDialog.setCancelable(true)

        showDialog.setPositiveButton(
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

        showDialog.setNegativeButton(
            "No"
        ) { dialog, id ->
            dialog.cancel()
        }
    }

    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        showDialog = AlertDialog.Builder(requireContext())
        alert = showDialog.create()
        progress = CustomProgressDialogue(requireContext())
        enableSwipeToDeleteAndUndo()
        // setValidDialog()
        adapter = ShoppingCartAdapter(products, ordersDetails, clickListener())
        getViewDataBinding().rvProducts.adapter = adapter
        getViewDataBinding().rvProducts.addOnItemTouchListener(
            RecyclerTouchListener(requireContext(),
                getViewDataBinding().rvProducts, object : ItemLongClickListener {
                    override fun onLongClick(position: Int, view: View) {
                        clickedPosition = position
                        //alert11.show()
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
                    cat3++
                    if (it.data.isNotEmpty()) {
                        cat3--
                        getViewDataBinding().isLoading = false
                        ordersDetails = it.data.map { it.value }.toMutableList()
                        products = it.data.map { it.key }.toMutableList()
                        adapter.addItems(products)
                        adapter.addOrderDetails(ordersDetails)
                    } else
                        if (cat3 == 3)
                            getViewDataBinding().textView.visibility = View.VISIBLE


                    progress.dismiss()
                }
                is DataState.Error<*> -> {

                }
            }
        })

        getViewModel().dataStateIncrementProducts.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Boolean> -> {
                    if (it.data) {
                        val product = adapter.getItem(clickedPosition)
                        product.quantity--
                        adapter.listView[clickedPosition]!!.product = product
                        val orderDetails = adapter.itemsOrderDetails[clickedPosition]
                        orderDetails.quantity++
                        adapter.listView[clickedPosition]!!.orderDetails = orderDetails
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
                        val product = adapter.getItem(clickedPosition)
                        product.quantity++
                        adapter.listView[clickedPosition]!!.product = product
                        val orderDetails = adapter.itemsOrderDetails[clickedPosition]
                        orderDetails.quantity--
                        adapter.listView[clickedPosition]!!.orderDetails = orderDetails
                    }

                }
                is DataState.Error<*> -> {

                }
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
        getViewDataBinding().orderBtn.setOnClickListener {
            findNavController().navigate(
                ShoppingCartFragmentDirections.actionShoppingCartFragmentToShoppingCartBottomCartFragment(
                    adapter.getTotalPrice()
                )
            )
        }

    }


    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    clickedPosition = position
                    removeFromShoppingCart(
                        adapter.getItem(clickedPosition),
                        adapter.itemsOrderDetails[clickedPosition]
                    )
                    adapter.removeItem(clickedPosition)
                    adapter.removeOrderDetails(clickedPosition)
                    if (adapter.getItems().isEmpty()) {
                        getViewDataBinding().textView.visibility = View.VISIBLE
                    }
//                    val item = adapter.getItem(position)
//                    adapter.removeItem(position)
//                    setValidDialog()
//                    alert=showDialog.create()
//                    alert.show()
//                val snackbar = Snackbar
//                    .make(
//                        coordinatorLayout,
//                        "Item was removed from the list.",
//                        Snackbar.LENGTH_LONG
//                    )
//                snackbar.setAction("UNDO") {
//                    //adapter.restoreItem(item, position)
//                    getViewDataBinding().rvProducts.scrollToPosition(position)
//                }
//                snackbar.setActionTextColor(Color.YELLOW)
//                snackbar.show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(getViewDataBinding().rvProducts)
    }


}