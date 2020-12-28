package com.example.e_commerce.ui.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, R>(private val items: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var listView: HashMap<Int, R> = HashMap()
    var list: List<T> = items
    var shoppingCartList: List<T> = emptyList()
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    private fun clearItems() {
        items.clear()
    }

    open fun setItems(items: List<T>) {
        clearItems()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    open fun addItems(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (items.size > 0) items.size else 0
    } // 1 for Binding Empty Item

    open fun getItems(): MutableList<T> {
        return items
    }

    fun getItem(position: Int): T {
        return getItems()[position]!!
    }

    fun removeItem(position: Int) {
        getItems().removeAt(position)!!
        notifyDataSetChanged()
    }

}
