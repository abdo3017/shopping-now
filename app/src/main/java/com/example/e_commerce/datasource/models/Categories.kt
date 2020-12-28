package com.example.e_commerce.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Categories(
    var id: String? = null,
    var name: String? = null,
    var image: String? = null
) : Parcelable