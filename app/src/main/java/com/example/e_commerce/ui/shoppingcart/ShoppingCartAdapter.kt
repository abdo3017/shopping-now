package com.example.e_commerce.ui.shoppingcart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.e_commerce.databinding.ItemProductListNewBinding
import com.example.e_commerce.datasource.models.OrderDetails
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseRecyclerViewAdapter
import com.example.e_commerce.ui.base.BaseViewHolder
import com.example.e_commerce.ui.base.ItemClickListener

class ShoppingCartAdapter(
    itemsProducts: MutableList<Products>,
    val itemsOrderDetails: MutableList<OrderDetails>,
    private val itemClickListener: ItemClickListener,
) :
    BaseRecyclerViewAdapter<Products, ItemProductListNewBinding>(itemsProducts) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemProductListNewBinding.inflate(
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

    fun getTotalPrice(): String {
        var price = 0.0
        this.listView.onEach {
            price += (it.value.itemPrice.text.toString().split(" ")[0]
                .toDouble() * it.value.iteamAmount.text.toString().toDouble())
        }
        return price.toString()
    }

    inner class ProductsViewHolder(
        private val binding: ItemProductListNewBinding,
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