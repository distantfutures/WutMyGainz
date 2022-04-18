package com.example.wutmygainz.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wutmygainz.network.DataObject

@Dao
interface DataObjectDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(crypto: DataObject)
    @Query("SELECT * from current_crypto_price_table WHERE crypto = :key")
    fun getSpotPrice(key: String): DataObject
    @Query("DELETE FROM current_crypto_price_table")
    fun deleteAll()
    @Query("SELECT * FROM current_crypto_price_table ORDER BY crypto DESC")
    fun getAllCoinPrices(): LiveData<List<DataObject>>
}