package com.example.e_commerce.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.example.e_commerce.databinding.ItemProductBinding
import com.example.e_commerce.datasource.models.Products
import com.example.e_commerce.ui.base.BaseRecyclerViewAdapter
import com.example.e_commerce.ui.base.BaseViewHolder
import com.example.e_commerce.ui.base.ItemClickListener


class SearchAdapter(
    itemsProducts: MutableList<Products>,
    var itemsSearchProducts: MutableList<Products>,
    private val itemClickListener: ItemClickListener,
) :
    BaseRecyclerViewAdapter<Products, ItemProductBinding>(itemsProducts), Filterable {
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

    override fun getFilter() = productFilter

    private val productFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                val filterPattern = constraint.toString().toLowerCase().trim()
                results.values =
                    itemsSearchProducts.filterNot {
                        !it.name!!.toLowerCase().trim().contains(filterPattern)
                    }
                return results
            }
            results.values = listOf<Products>()
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            getItems().clear()
            getItems().addAll(results!!.values as List<Products>)
            notifyDataSetChanged()
        }

    }
}