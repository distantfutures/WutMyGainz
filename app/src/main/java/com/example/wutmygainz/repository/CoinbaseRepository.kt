package com.example.wutmygainz.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.wutmygainz.database.AppDatabase
import com.example.wutmygainz.network.CoinbaseApi
import com.example.wutmygainz.network.Data
import com.example.wutmygainz.network.DataObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG = "CBRepoCheck"
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
        return price
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