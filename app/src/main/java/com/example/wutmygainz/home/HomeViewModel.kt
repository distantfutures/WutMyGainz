package com.example.wutmygainz.home

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.database.AppDatabase
import com.example.wutmygainz.formatDate
import com.example.wutmygainz.formatToMilli
import com.example.wutmygainz.repository.CoinbaseRepository
import com.example.wutmygainz.repository.InvestmentsRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeVMCheck"
@RequiresApi(Build.VERSION_CODES.N)
class HomeViewModel(application: Application) : ViewModel() {

    private var _historicPrice = MutableLiveData<Double?>()
    val historicPrice: LiveData<Double?>
        get() = _historicPrice

    private var _currentPrice = MutableLiveData<Double>()
    val currentPrice: LiveData<Double>
        get() = _currentPrice

    private var _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String>
        get() = _selectedDate

    private var _currencyPair = MutableLiveData<String>()
    val currencyPair: LiveData<String>
        get() = _currencyPair

    private var _theGainzPercent = MutableLiveData<Double>()
    val theGainzPercent: LiveData<Double>
        get() = _theGainzPercent

    private var _theGainzCurrency = MutableLiveData<Double?>()
    val theGainzCurrency: LiveData<Double?>
        get() = _theGainzCurrency

    private var _investedPrice = MutableLiveData<Double?>()
    private val investedPrice: LiveData<Double?>
        get() = _investedPrice

    private var _isToday = MutableLiveData<Boolean>()
    val isToday: LiveData<Boolean> = _isToday

    // Calls database to get Room Database into listData attribute in RecyclerView
    private val investmentsRepository = InvestmentsRepository(AppDatabase.getInstance(application))
    val getAllInvestments = investmentsRepository.getAllInvestments

    private val coinbaseRepository = CoinbaseRepository(AppDatabase.getInstance(application))

    var theCurrentPrice = ""
    val allCurrentPrices: MutableMap<String, Double> = mutableMapOf()

    var startYear = 0
    var startMonth = 0
    var startDay = 0

    var pickYear = 0
    var pickMonth = 0
    var pickDay = 0

    init {
        _currentPrice.value = 0.0
        _historicPrice.value = 0.0
        _theGainzPercent.value = 0.0
        _currencyPair.value = "BTC-USD"
        _theGainzCurrency.value = 0.0
        _isToday.value = true
        setInitSpotPrice()
        getCurrentDate()
        Log.i(TAG, "Initialized!")
    }

    fun isToday() {
        _isToday.value = pickYear == startYear && pickMonth == startMonth && pickDay == startDay
        Log.i(TAG, "IsToday: $pickYear-$pickMonth-$pickDay vs $startYear-$startMonth-$startDay")
    }

    fun refreshGainz() {
        // Needs to wait for new API CALL
        if (!_isToday.value!!) {
            Log.i(TAG, "REFRESH!!")
            viewModelScope.launch {
                setHistoricPrice() // causes infinity problem
                Log.i(TAG, "AWAITING!")
                val calculate = async { calculateTheGainzPercent() }
                calculate.join()
            }
        } else {
            clearHistoricPrice()
            clearGainzCurrency()
            clearGainzPercent()
        }
        onSetInvestedAmount(_investedPrice.value)
//        Log.i(TAG, " Refresh: ${_investedPrice.value}")
    }

    fun onSetPairs(pairs: String) {
        _currencyPair.value = pairs
        val coin = pairs.replace("-USD", "")
        setSpotPriceFromDb(coin)
        refreshGainz()
//        Log.i(TAG, "OnSetPairs: ${_currencyPair.value} ${_isToday.value} IsToday")
    }
    fun onDeleteTable() {
        viewModelScope.launch {
            coinbaseRepository.deleteTable()
        }
    }

    suspend fun calculateTheGainzPercent() {
        delay(500)
        Log.i(TAG, "CALCULATE!")
        val current = _currentPrice.value
        val historic = _historicPrice.value
        val difference = current?.minus(historic!!)
        val gainzIntPercent = (difference?.div(historic!!))?.times(100)
        if (difference!! > 0) {
            _theGainzPercent.value = gainzIntPercent!!
        } else {
            _theGainzPercent.value = gainzIntPercent!!
        }
        Log.i(TAG, "Gainz Percent: $gainzIntPercent" +
                "Current: $current" +
                "Historic: $historic" +
                "Difference: $difference")
    }

    fun pickedDate(year: Int, month: Int, day: Int) {
        val pickDate = Calendar.getInstance()
        pickYear = year
        pickMonth = month
        pickDay = day
        pickDate.set(pickYear, pickMonth, pickDay)
        isToday()
        setSelectedDate(pickDate)
        refreshGainz()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun trackInvestments() {
        viewModelScope.launch {
            val gatherData = gatherInvestmentData()
            investmentsRepository.insertNewInvestment(gatherData)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun gatherInvestmentData(): Investments {
        val newInvestment = Investments()
        newInvestment.purchaseDate = formatToMilli(selectedDate.value!!)
        newInvestment.currencyPair = currencyPair.value!!
        newInvestment.investedPrice = investedPrice.value!!
        if (_isToday.value!!) {
            newInvestment.historicPrice = _currentPrice.value!!
        } else {
            newInvestment.historicPrice = _historicPrice.value!!
        }
//        Log.i(TAG, "gatherInvestmentData: $newInvestment")
        return newInvestment
    }

    fun onSetInvestedAmount(cost: Double?) {
        _investedPrice.value = cost
        Log.i(TAG, "onSetInvested: $cost")
        setGainzCurrency()
    }

    private fun getCurrentDate() {
        val currentDate = Calendar.getInstance()
        startYear = currentDate.get(Calendar.YEAR)
        startMonth = currentDate.get(Calendar.MONTH)
        startDay = currentDate.get(Calendar.DAY_OF_MONTH)
        currentDate.set(startYear, startMonth, startDay)
        setSelectedDate(currentDate)
        setSpotPriceFromMap()
//        Log.i(TAG, "Initial Date: $startYear $startMonth $startDay")
    }

    fun getAllSpotPrices(pairs: String) {
        viewModelScope.launch {
            val spotPrice = coinbaseRepository.getAllSpotPrices(pairs)
            allCurrentPrices[pairs] = spotPrice
//            Log.i(TAG, "MAP TEST: $allCurrentPrices")
        }
    }
    // Set LiveData
    fun setGainzCurrency() {
        val cost = _investedPrice.value
        val currencyGainz = cost?.let { (_theGainzPercent.value?.toDouble()?.div(100))?.times(it) }
        if (cost == null) {
            _theGainzCurrency.value = 0.00
//            Log.i(TAG, "NULL SetInvested Amount: ${_investedPrice.value}")
        } else {
            // Add format with commas. Change to strings?
            _theGainzCurrency.value = currencyGainz
//            Log.i(TAG, "SetInvested Amount: ${_investedPrice.value}")
        }
    }
    private fun setSelectedDate(selectedDate: Calendar) {
        _selectedDate.value = formatDate(selectedDate)
    }
    private fun setInitSpotPrice() {
        viewModelScope.launch {
            val spot = coinbaseRepository.getSpotPrice(_currencyPair.value!!)
            _currentPrice.value = spot
        }
    }
    fun setSpotPriceFromMap() {
        val pair = _currencyPair.value
        _currentPrice.value = allCurrentPrices[pair]
    }
    fun setSpotPriceFromDb(coin: String){
        viewModelScope.launch {
            val spotPrice = coinbaseRepository.getSpotPriceDB(coin)
            _currentPrice.value = spotPrice
        }
    }
    fun setHistoricPrice() {
        val pair = _currencyPair.value!!
        val date = _selectedDate.value!!
        viewModelScope.launch {
            val historicPrice = coinbaseRepository.getHistoricPrice(pair, date)
            Log.i(TAG, "Historic Price Awaiting!")
            _historicPrice.value = historicPrice
            Log.i(TAG, "Historic Price Set!")
        }
    }
    fun clearHistoricPrice() {
        _historicPrice.value = 0.00
    }
    fun clearGainzCurrency() {
        _theGainzCurrency.value = 0.0
    }
    fun clearGainzPercent() {
        _theGainzPercent.value = 0.0
    }
}