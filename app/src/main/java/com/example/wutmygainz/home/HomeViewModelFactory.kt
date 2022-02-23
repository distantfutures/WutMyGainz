package com.example.wutmygainz.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.database.InvestmentsDatabaseDAO

class HomeViewModelFactory(private val datasource: InvestmentsDatabaseDAO) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.N)
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}