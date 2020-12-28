package com.example.e_commerce.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDetails(
    var orderId: String? = null,
    var productId: String? = null,
    var quantity: Long = 0
) : Parcelable