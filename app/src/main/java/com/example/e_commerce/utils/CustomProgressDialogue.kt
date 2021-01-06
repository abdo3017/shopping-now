package com.example.e_commerce.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.example.e_commerce.R

@SuppressLint("InflateParams")
class CustomProgressDialogue(context: Context) : Dialog(context) {
    init {
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_product2, null
        )
        setContentView(view)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }
}