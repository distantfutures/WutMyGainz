package com.example.wutmygainz.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvestmentsRepository(private val database: AppDatabase) {
    val getAllInvestments:LiveData<List<Investments>> = database.investmentDatabaseDao.getAllInvestments()

    suspend fun insertNewInvestment(newInvestment: Investments) {
        withContext(Dispatchers.IO) {
            database.investmentDatabaseDao.insertNew(newInvestment)
            Log.i("CheckHomeViewModel", "Room: $newInvestment")
        }
    }
}