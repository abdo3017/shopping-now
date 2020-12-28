package com.example.e_commerce.ui.revieworder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.e_commerce.databinding.ItemProductList2Binding
import com.example.e_commerce.databinding.ItemProductListBinding
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseRecyclerViewAdapter
import com.example.e_commerce.ui.base.BaseViewHolder
import com.example.e_commerce.ui.base.ItemClickListener

class ReviewOrderAdapter(
    itemsProducts: MutableList<Products>,
    val itemsOrderDetails: MutableList<OrderDetails>,
    private val itemClickListener: ItemClickListener,
) :
    BaseRecyclerViewAdapter<Products, ItemProductList2Binding>(itemsProducts) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemProductList2Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )
    }

    fun addOrderDetails(itemsOrderDetails: MutableList<OrderDetails>) {
        this.itemsOrderDetails.addAll(itemsOrderDetails)
        notifyDataSetChanged()
    }

    fun removeOrderDetails(position: Int) {
        itemsOrderDetails.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ProductsViewHolder(
        private val binding: ItemProductList2Binding,
        private val itemClickListener: ItemClickListener
    ) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            listView[position] = binding
            binding.product = getItem(position)
            binding.orderDetails = itemsOrderDetails[position]
            binding.position = position
            binding.listener = itemClickListener
            binding.executePendingBindings()
        }

    }
}