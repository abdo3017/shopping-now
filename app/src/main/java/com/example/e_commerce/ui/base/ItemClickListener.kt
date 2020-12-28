package com.example.e_commerce.ui.base

import android.view.View

//We use interface to notify the activity with every selection like onItemSelected , onBookmarkSelected
class ItemClickListener(private val clickListener: (position: Int, view: View) -> Unit) {
    fun onClick(position: Int, view: View) =
        clickListener(position, view)
}