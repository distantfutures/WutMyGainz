package com.example.wutmygainz

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wutmygainz.network.CoinbaseApi
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
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

    private var _theGainzPercent = MutableLiveData<String>()
    val theGainzPercent: LiveData<String>
        get() = _theGainzPercent

    private var _theGainzCurrency = MutableLiveData<Double?>()
    val theGainzCurrency: LiveData<Double?>
        get() = _theGainzCurrency

    private var _investedCost = MutableLiveData<Double?>()
    val investedCost: LiveData<Double?>
        get() = _investedCost

    var startYear = 0
    var startMonth = 0
    var startDay = 0

    var pickYear = 0
    var pickMonth = 0
    var pickDay = 0

    init {
        _currencyPair.value = "BTC-USD"
        _theGainzCurrency.value = 0.00
        getDateCalendar()
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
                calculateTheGainz(currentDouble, historicDouble)
            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
    }

    fun onSetInvestedAmount(cost: Double?) {
        _investedCost.value = cost
        val currencyGainz = cost?.let { (_theGainzPercent.value?.toDouble()?.div(100))?.times(it) }
        if (cost == null) {
            _theGainzCurrency.value = 0.00
        } else {
            val gainzString = String.format("%.2f", currencyGainz).toDouble()
            // Add format with commas. Change to strings?
            _theGainzCurrency.value = gainzString
        }
        Log.i("CheckSelectedPairs", "${_investedCost.value}")
    }

    fun onSetSelectedPairs(pairs: String) {
        _currencyPair.value = pairs
        getCoinPrices()
        Log.i("CheckSelectedPairs", "${_currencyPair.value}")
    }
    private fun Double.currencyFormat(): String {
        val decimalFormat = DecimalFormat("#,###,##0.00")
        return decimalFormat.format(this).toString()
    }
    private fun getDateCalendar() {
        val currentDate = Calendar.getInstance()
        startYear = currentDate.get(Calendar.YEAR)
        startMonth = currentDate.get(Calendar.MONTH)
        startDay = currentDate.get(Calendar.DAY_OF_MONTH)
        currentDate.set(startYear, startMonth, startDay)
        val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = formatterDate.format(currentDate.time)
        Log.i("CheckHomeViewModel", "$startYear $startMonth $startDay")
        getCoinPrices()
        onSetInvestedAmount(_investedCost.value)
    }
    fun pickedDate(year: Int, month: Int, day: Int) {
        val pickDate = Calendar.getInstance()
        pickYear = year
        pickMonth = month
        pickDay = day
        pickDate.set(pickYear, pickMonth, pickDay)
        // format Date
        val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = formatterDate.format(pickDate.time)
        getCoinPrices()
    }
    private fun calculateTheGainz(current: Double?, historic: Double?) {
        val difference = current?.minus(historic!!)
        val gainzIntPercent = (difference?.div(historic!!))?.times(100)
        val gainzString = String.format("%.2f", gainzIntPercent)
        if (difference!! > 0) {
            _theGainzPercent.value = "+$gainzString"
        } else {
            _theGainzPercent.value = gainzString
        }
        Log.i("CheckHomeViewModel", "$$gainzString")
    }
}