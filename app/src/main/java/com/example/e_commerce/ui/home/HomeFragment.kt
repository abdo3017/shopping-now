package com.example.e_commerce.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.datasource.dbservice.FireBaseRepository
import com.example.e_commerce.datasource.models.Categories
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.state.DataState
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.ui.base.ItemClickListener
import com.example.e_commerce.ui.main.MainActivity
import com.example.e_commerce.utils.CustomProgressDialogue
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
    lateinit var fireBaseService: FireBaseRepository
    private var categories: MutableList<Categories> = mutableListOf()
    private var products: MutableList<Products> = mutableListOf()
    private lateinit var adapter: HomeAdapter
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
        progress = CustomProgressDialogue(requireContext())
        getViewDataBinding().rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        getViewDataBinding().rvProducts.adapter = adapter
    }

    private fun clickListener() = ItemClickListener { position: Int, view: View ->
        if (view.id == R.id.add_button) {
            Log.d("ererererer", adapter.getItems()[position].toString())
            if (adapter.listView[position]!!.isFav != true) {
                adapter.listView[position]!!.isFav = true
                addToShoppingCart(adapter.getItem(position))
            }
        } else {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
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
                    getProductsDataByCategory(categories[0])
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
                    adapter.setItems(it.data.shuffled())
                    progress.dismiss()
                }
                is DataState.Error<*> -> {

                }
            }
        })

        getViewModel().dataStateAddToShoppingCart.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success<Products> -> {

                }
                is DataState.Error<*> -> {

                }
            }
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
            selectCategory(1)
            getProductsDataByCategory(categories[0])
        }
        getViewDataBinding().tvElectronics.setOnClickListener {
            selectCategory(2)
            getProductsDataByCategory(categories[1])
        }
        getViewDataBinding().tvFurniture.setOnClickListener {
            selectCategory(3)
            getProductsDataByCategory(categories[2])
        }
        getViewDataBinding().openDrawer.setOnClickListener {
            val myActivity = requireActivity() as MainActivity
            myActivity.getViewDataBinding().drawerLayout.openDrawer(GravityCompat.START)
        }

    }


    @SuppressLint("ResourceAsColor")
    private fun selectCategory(num: Int) {
        when (num) {
            1 -> {
                getViewDataBinding().clothes.setBackgroundResource(R.drawable.rounded_view)
                getViewDataBinding().electronics.setBackgroundResource(0)
                getViewDataBinding().furniture.setBackgroundResource(0)
                getViewDataBinding().tvClothes.setTextColor(Color.parseColor("#EEE7Df"))
                getViewDataBinding().tvElectronics.setTextColor(Color.parseColor("#41372D"))
                getViewDataBinding().tvFurniture.setTextColor(Color.parseColor("#41372D"))
            }
            2 -> {
                getViewDataBinding().tvElectronics.setTextColor(Color.parseColor("#EEE7Df"))
                getViewDataBinding().tvClothes.setTextColor(Color.parseColor("#41372D"))
                getViewDataBinding().tvFurniture.setTextColor(Color.parseColor("#41372D"))
                getViewDataBinding().electronics.setBackgroundResource(R.drawable.rounded_view)
                getViewDataBinding().clothes.setBackgroundResource(0)
                getViewDataBinding().furniture.setBackgroundResource(0)
            }
            else -> {
                getViewDataBinding().tvFurniture.setTextColor(Color.parseColor("#EEE7Df"))
                getViewDataBinding().tvElectronics.setTextColor(Color.parseColor("#41372D"))
                getViewDataBinding().tvClothes.setTextColor(Color.parseColor("#41372D"))
                getViewDataBinding().furniture.setBackgroundResource(R.drawable.rounded_view)
                getViewDataBinding().electronics.setBackgroundResource(0)
                getViewDataBinding().clothes.setBackgroundResource(0)

            }
        }
    }

    override fun getBackPressed() = false
}
