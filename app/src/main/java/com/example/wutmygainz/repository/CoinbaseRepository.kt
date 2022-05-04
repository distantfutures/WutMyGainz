package com.example.wutmygainz.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.wutmygainz.database.AppDatabase
import com.example.wutmygainz.network.CoinbaseApi
import com.example.wutmygainz.network.Data
import com.example.wutmygainz.network.DataObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "CBRepoCheck"
class CoinbaseRepository(private val database: AppDatabase) {
    val getAllSpotPrices: LiveData<List<DataObject>> = database.dataObjectDao.getAllCoinPrices()

    suspend fun getAllSpotPrices(pairs: String): Double {
        var price = 0.0
        withContext(Dispatchers.IO) {
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(pairs)
            if (responseSpot.isSuccessful) {
                val data = responseSpot.body()?.data
                insertSpotPrice(data!!)
                val spotPriceList = responseSpot.body()?.data?.amount
                price = spotPriceList!!
            }
        }
        Log.i(TAG, "Price Check: $price")
        return price
    }

    suspend fun getHistoricPrice(pairs: String, date: String): Double {
        var historicPrice = 0.0
        withContext(Dispatchers.IO) {
            val responseHistoric = CoinbaseApi.retrofitService.getHistoricCoinPrice(pairs, date)
            if (responseHistoric.isSuccessful) {
                val response = responseHistoric.body()?.data
                // Insert Historic Price into DB
                historicPrice = response?.amount!!
            }
        }
        return historicPrice
    }
    private suspend fun insertSpotPrice(data: Data) {
        withContext(Dispatchers.IO) {
            val dataObject = DataObject(data)
            database.dataObjectDao.insert(dataObject)
        }
    }
    suspend fun deleteTable() {
        withContext(Dispatchers.IO) {
            database.dataObjectDao.deleteAll()
        }
    }
    suspend fun getSpotPriceOf(coin: String): Double {
        var price = 0.0
        withContext(Dispatchers.IO) {
            price = database.dataObjectDao.getSpotPrice(coin).data.amount
            Log.i(TAG, "Pair $coin")
        }
        return price
    }
}