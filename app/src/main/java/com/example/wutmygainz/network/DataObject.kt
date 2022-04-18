package com.example.wutmygainz.network

import androidx.room.Embedded

data class DataObject(
    val data: Data
)

data class Data(
    val base: String,
    val currency: String,
    val amount: Double
)
