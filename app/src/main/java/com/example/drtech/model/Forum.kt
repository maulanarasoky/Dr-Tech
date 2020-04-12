package com.example.drtech.model

data class Forum(
    val id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
    val tags: String? = "-",
    val hardware: String? = "-",
    val views: Int? = 0,
    val userId: String? = ""
)