package com.example.e_commerce.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Products(
    var id: String? = null,
    var name: String? = null,
    var price: Double = 0.0,
    var quantity: Long = 0,
    var catId: String? = null,
    var image: String? = null,
    var description: String? = null,
) : Parcelable