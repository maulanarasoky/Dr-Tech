package com.example.drtech.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forum(
    val id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
    val tags: MutableList<String>? = null,
    val hardware: MutableList<String>? = null,
    val views: Int? = 0,
    val userId: String? = ""
): Parcelable