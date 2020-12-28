package com.example.e_commerce.ui.base

import android.view.View

//We use interface to notify the activity with every selection like onItemSelected , onBookmarkSelected
interface ItemLongClickListener {
    fun onLongClick(position: Int, view: View)
}