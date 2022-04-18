package com.example.wutmygainz.repository

import androidx.lifecycle.LiveData
import com.example.wutmygainz.database.AppDatabase
import com.example.wutmygainz.network.CoinbaseApi
import com.example.wutmygainz.network.DataObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinbaseRepository(private val database: AppDatabase) {
    val getAllSpotPrices: LiveData<List<DataObject>> = database.dataObjectDao.getAllCoinPrices()

    suspend fun getAllSpotPrices(pairs: String): Double {
        var price = 0.0
        withContext(Dispatchers.IO) {
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(pairs)
            if (responseSpot.isSuccessful) {
                val spotPriceList = responseSpot.body()?.data?.amount
                price = spotPriceList!!
//                database.dataObjectDao.insert(spotPriceList)
            }
        }
        return price
    }
}