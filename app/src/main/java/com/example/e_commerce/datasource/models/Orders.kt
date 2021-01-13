package com.example.e_commerce.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Orders(
    var id: String? = null,
    var orderDate: String? = null,
    var address: String? = null,
    var customerId: String? = null,
    var submitted: Boolean? = false,
    var delevereded: String? = "not delivered"
) : Parcelable