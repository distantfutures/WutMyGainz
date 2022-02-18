package com.example.wutmygainz.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cryptocurrency_investment_table")
data class Investments(
    @PrimaryKey(autoGenerate = true)
    var investmentId : Long = 0L,

    @ColumnInfo(name = "purchase_date")
    var purchaseDate : Long,

    @ColumnInfo(name = "currency_pair")
    var currencyPair : String,

    @ColumnInfo(name = "invested_price")
    var investedPrice : Double,

    @ColumnInfo(name = "historic_price")
    var historicPrice : Double,

    @ColumnInfo(name = "current_price")
    var currentPrice : Double,
)