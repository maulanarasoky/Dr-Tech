package com.example.drtech.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    val id: String? = "",
    val name: String? = "",
    val type: String? = "",
    val business: String? = "",
    val skills: MutableList<String>? = null,
    val status: String? = "",
    val phoneNumber: String? = "",
    val favorite: Int? = 0
) : Parcelable