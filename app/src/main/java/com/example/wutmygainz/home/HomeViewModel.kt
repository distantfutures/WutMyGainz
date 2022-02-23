package com.example.wutmygainz.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.database.InvestmentsDatabaseDAO
import com.example.wutmygainz.network.CoinbaseApi
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class HomeViewModel(private val database: InvestmentsDatabaseDAO) : ViewModel() {

    private val viewModelJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private var _investedPrice = MutableLiveData<Double?>()
    val investedPrice: LiveData<Double?>
        get() = _investedPrice

    var startYear = 0
    var startMonth = 0
    var startDay = 0

    var pickYear = 0
    var pickMonth = 0
    var pickDay = 0

    init {
        _currentPrice.value = "00.00"
        _historicPrice.value = "00.00"
        _theGainzPercent.value = "00.00"
        _currencyPair.value = "BTC-USD"
        _theGainzCurrency.value = 0.00
        getDateCalendar()
        Log.i("CheckViewModel", "Initialized!")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun trackInvestments() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val gatherData = gatherInvestmentData()
                insertNewInvestment(gatherData)
            }
        }
    }


    private fun gatherInvestmentData(): Investments {
        val newInvestment = Investments()
        newInvestment.purchaseDate = formatToMilli(selectedDate.value!!)
        newInvestment.currencyPair = currencyPair.value!!
        newInvestment.investedPrice = investedPrice.value!!
        newInvestment.historicPrice = unformatCurrency(historicPrice.value!!)
        newInvestment.currentPrice = unformatCurrency(currentPrice.value!!)
        Log.i("CheckHomeViewModel", "gatherInvestmentData: $newInvestment")
        return newInvestment
    }
    private suspend fun insertNewInvestment(newInvestment: Investments) {
        withContext(Dispatchers.IO) {
            database.insertNew(newInvestment)
            val check = newInvestment
            Log.i("CheckHomeViewModel", "Room: $check")
        }
    }

    private fun getCoinPrices() {
        coroutineScope.launch {
            val responseHistoric = CoinbaseApi.retrofitService.getHistoricCoinPrice(_currencyPair.value!!, _selectedDate.value!!)
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(_currencyPair.value!!)

            if (responseHistoric.isSuccessful) {
                val historicDouble = responseHistoric.body()?.data?.amount
                _historicPrice.value = historicDouble?.currencyFormat()
                Log.i("CheckViewModel", "API SERVICE - Historic: ${responseHistoric.body()?.data?.amount}")

                val currentDouble = responseSpot.body()?.data?.amount
                _currentPrice.value = currentDouble?.currencyFormat()
                Log.i("CheckViewModel", "API SERVICE - Current: ${responseSpot.body()?.data?.amount}")
                calculateTheGainz(currentDouble, historicDouble)
            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
    }

    private fun getDateCalendar() {
        val currentDate = Calendar.getInstance()
        startYear = currentDate.get(Calendar.YEAR)
        startMonth = currentDate.get(Calendar.MONTH)
        startDay = currentDate.get(Calendar.DAY_OF_MONTH)
        currentDate.set(startYear, startMonth, startDay)
        val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = formatterDate.format(currentDate.time)
        Log.i("CheckViewModel", "Initial Date: $startYear $startMonth $startDay")
        getCoinPrices()
    }

    fun onSetInvestedAmount(cost: Double?) {
        _investedPrice.value = cost
        val currencyGainz = cost?.let { (_theGainzPercent.value?.toDouble()?.div(100))?.times(it) }
        if (cost == null) {
            _theGainzCurrency.value = 0.00
            Log.i("CheckViewModel", "NULL SetInvested Amount: ${_investedPrice.value}")
        } else {
            // Add format with commas. Change to strings?
            _theGainzCurrency.value = currencyGainz
            Log.i("CheckViewModel", "SetInvested Amount: ${_investedPrice.value}")
        }
    }
    suspend fun refreshGainz() {
        delay(1000)
        // Needs to wait for new API CALL
        onSetInvestedAmount(_investedPrice.value)
        Log.i("CheckViewModel", " Refresh: ${_investedPrice.value}")
    }

    fun onSetSelectedPairs(pairs: String) {
        _currencyPair.value = pairs
        getCoinPrices()
        Log.i("CheckViewModel", "Selected Pairs: ${_currencyPair.value}")
    }
    private fun Double.currencyFormat(): String {
        val decimalFormat = DecimalFormat("#,###,##0.00")
        return decimalFormat.format(this)
    }
    private fun unformatCurrency(price: String): Double {
        val currencyAsDouble = price.replace(",", "")
        return currencyAsDouble.toDouble()
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
        coroutineScope.launch {
            val refresh = async { refreshGainz() }
            refresh.await()
        }
//        formatToMilli(formatterDate.format(pickDate.time))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToMilli(date: String): Long {
        val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateInMilliseconds = formatterDate.parse(date)
        Log.i("CheckHomeViewModel", "Date in milli: $dateInMilliseconds")
        return dateInMilliseconds.time
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
        Log.i("CheckViewModel", "Gainz Percentage: $$gainzString")
    }
}