package com.example.e_commerce.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customers(
    var id: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var password: String? = null,
    var gender: String? = null,
    var birthDate: String? = null,
    var job: String? = null,
    var image: String? = null
) : Parcelable