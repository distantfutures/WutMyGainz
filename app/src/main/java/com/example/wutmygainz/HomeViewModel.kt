package com.example.wutmygainz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wutmygainz.network.CoinbaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val viewModelJob = Job()
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var _historicPrice = MutableLiveData<String>()
    val historicPrice: LiveData<String>
        get() = _historicPrice

    private var _currentPrice = MutableLiveData<String>()
    val currentPrice: LiveData<String>
        get() = _currentPrice

    private var _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String>
        get() = _selectedDate

    private var _currencyPair = MutableLiveData<String>()
    val currencyPair: LiveData<String>
        get() = _currencyPair

    init {
        _currencyPair.value = "BTC-USD"
        _selectedDate.value = "2017-01-16"
        getCoinPrices()
    }

    private fun getCoinPrices() {
        coroutineScope.launch {
            val responseHistoric = CoinbaseApi.retrofitService.getHistoricCoinPrice(_currencyPair.value!!, _selectedDate.value!!)
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(_currencyPair.value!!)

            if (responseHistoric.isSuccessful) {
                _historicPrice.value = responseHistoric.body()?.data?.amount
                Log.i("CheckAPI Service", "Historic: ${responseHistoric.body()?.data?.amount}")

                _currentPrice.value = responseSpot.body()?.data?.amount
                Log.i("CheckAPI Service", "Current: ${responseSpot.body()?.data?.amount}")
            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
    }

    fun onSetSelectedPairs(pairs: String) {
        _currencyPair.value = pairs
        Log.i("CheckSelectedPairs", "${_currencyPair.value}")
    }
}