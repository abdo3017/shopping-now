package com.app.movie.presentation.base

import android.view.View

interface BaseItemListener<T> {
    fun onItemClick(view: View, item: T)
}