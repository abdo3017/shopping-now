package com.example.e_commerce.ui.myorders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.e_commerce.databinding.ItemOrderBinding
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Orders
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseRecyclerViewAdapter
import com.example.e_commerce.ui.base.BaseViewHolder
import com.example.e_commerce.ui.base.ItemClickListener

class MyOrderAdapter(
    itemsProducts: MutableList<Orders>,
    var itemsOrdersDetails: HashMap<Orders, HashMap<Products, OrderDetails>>,
    private val itemClickListener: ItemClickListener,
) :
    BaseRecyclerViewAdapter<Orders, ItemOrderBinding>(itemsProducts) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )
    }

    fun addOrderDetails(itemsOrderDetails: HashMap<Orders, HashMap<Products, OrderDetails>>) {
        this.itemsOrdersDetails = itemsOrderDetails
        notifyDataSetChanged()
    }


    inner class ProductsViewHolder(
        private val binding: ItemOrderBinding,
        private val itemClickListener: ItemClickListener
    ) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            listView[position] = binding
            binding.order = getItem(position)
            binding.position = position
            binding.listener = itemClickListener
            binding.adapter =
                ProductsOfOrderAdapter(itemsOrdersDetails[getItem(position)]!!.mapNotNull { it.key }
                    .toMutableList(),
                    itemsOrdersDetails[getItem(position)]!!.mapNotNull { it.value }.toMutableList(),
                    itemClickListener
                )
            binding.executePendingBindings()
        }

    }
}