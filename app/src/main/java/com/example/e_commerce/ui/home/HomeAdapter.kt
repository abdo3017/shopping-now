package com.example.e_commerce.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.e_commerce.databinding.ItemProductBinding
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseRecyclerViewAdapter
import com.example.e_commerce.ui.base.BaseViewHolder
import com.example.e_commerce.ui.base.ItemClickListener

class HomeAdapter(
    items: MutableList<Products>,
    private val itemClickListener: ItemClickListener
) :
    BaseRecyclerViewAdapter<Products, ItemProductBinding>(items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )

    }

    inner class ProductsViewHolder(
        private val binding: ItemProductBinding,
        private val itemClickListener: ItemClickListener
    ) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            listView[position] = binding
            binding.isFav = false
            binding.product = getItem(position)
            binding.position = position
            binding.listener = itemClickListener
            binding.executePendingBindings()
        }

    }
}