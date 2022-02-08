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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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

    var startYear = 0
    var startMonth = 0
    var startDay = 0

    var pickYear = 0
    var pickMonth = 0
    var pickDay = 0

    init {
        _currencyPair.value = "BTC-USD"
        getdateCalendar()
        getCoinPrices()
    }

    private fun getCoinPrices() {
        coroutineScope.launch {
            val responseHistoric = CoinbaseApi.retrofitService.getHistoricCoinPrice(_currencyPair.value!!, _selectedDate.value!!)
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(_currencyPair.value!!)

            if (responseHistoric.isSuccessful) {
                val historicDouble = responseHistoric.body()?.data?.amount
                _historicPrice.value = historicDouble?.currencyFormat()
                Log.i("CheckAPI Service", "Historic: ${responseHistoric.body()?.data?.amount}")

                val currentDouble = responseSpot.body()?.data?.amount
                _currentPrice.value = currentDouble?.currencyFormat()
                Log.i("CheckAPI Service", "Current: ${responseSpot.body()?.data?.amount}")
            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
    }

    fun onSetSelectedPairs(pairs: String) {
        _currencyPair.value = pairs
        getCoinPrices()
        Log.i("CheckSelectedPairs", "${_currencyPair.value}")
    }
    fun Double.currencyFormat(): String {
        val decimalFormat = DecimalFormat("#,###,##0.00")
        return decimalFormat.format(this).toString()
    }
    fun setNewDateTime(): String? {
        getdateCalendar()
        var dateString = ""

        return dateString
    }
    private fun getdateCalendar() {
        val currentDate = Calendar.getInstance()
        startYear = currentDate.get(Calendar.YEAR)
        startMonth = currentDate.get(Calendar.MONTH)
        startDay = currentDate.get(Calendar.DAY_OF_MONTH)
        currentDate.set(startYear, startMonth, startDay)
        val formatterDate = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        _selectedDate.value = formatterDate.format(currentDate.time)
        Log.i("CheckHomeViewModel", "$startYear $startMonth $startDay")
    }
    fun pickedDate(year: Int, month: Int, day: Int) {
        val pickDate = Calendar.getInstance()
        pickYear = year
        pickMonth = month
        pickDay = day
        pickDate.set(pickYear, pickMonth, pickDay)
        // format Date
        val formatterDate = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        _selectedDate.value = formatterDate.format(pickDate.time)
    }
}