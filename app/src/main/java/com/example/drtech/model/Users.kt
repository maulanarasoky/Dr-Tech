package com.example.drtech.model

data class Users (
    val id: String? = "",
    val name: String? = "",
    val type: String? = "",
    val business: String? = "",
    val skills: MutableList<String>? = null,
    val status: String? = "",
    val ratings: Int? = 0
)