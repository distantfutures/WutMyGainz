package com.example.wutmygainz.network

import androidx.room.*

@Entity(tableName = "current_crypto_price_table")
data class DataObject(
    @PrimaryKey (autoGenerate = false)
    @Embedded
    val data: Data
)
data class Data(

    @ColumnInfo(name = "crypto")
    val base : String,

    @ColumnInfo(name = "usd")
    val currency : String,

    @ColumnInfo(name = "current_price")
    val amount : Double
)
