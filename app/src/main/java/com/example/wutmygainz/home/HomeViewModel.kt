package com.example.wutmygainz.home

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.database.AppDatabase
import com.example.wutmygainz.formatCurrency
import com.example.wutmygainz.formatToMilli
import com.example.wutmygainz.repository.CoinbaseRepository
import com.example.wutmygainz.repository.InvestmentsRepository
import com.example.wutmygainz.unformatCurrency
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeVMCheck"
@RequiresApi(Build.VERSION_CODES.N)
class HomeViewModel(application: Application) : ViewModel() {

    private var _historicPrice = MutableLiveData<String?>()
    val historicPrice: LiveData<String?>
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
    private val investedPrice: LiveData<Double?>
        get() = _investedPrice

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
        _currentPrice.value = "00.00"
        _historicPrice.value = "00.00"
        _theGainzPercent.value = "00.00"
        _currencyPair.value = "BTC-USD"
        _theGainzCurrency.value = 0.00
        getSpotPriceOf()
        getCurrentDateCalendar()
        Log.i(TAG, "Initialized!")
    }

    private fun getCurrentDateCalendar() {
        val currentDate = Calendar.getInstance()
        startYear = currentDate.get(Calendar.YEAR)
        startMonth = currentDate.get(Calendar.MONTH)
        startDay = currentDate.get(Calendar.DAY_OF_MONTH)
        currentDate.set(startYear, startMonth, startDay)
        val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = formatterDate.format(currentDate.time)
        Log.i(TAG, "Initial Date: $startYear $startMonth $startDay")
        setSpotPrice()
    }

    suspend fun refreshGainz() {
        delay(1000)
        // Needs to wait for new API CALL
        onSetInvestedAmount(_investedPrice.value)
        Log.i(TAG, " Refresh: ${_investedPrice.value}")
    }

    fun onSetSelectedPairs(pairs: String) {
        _currencyPair.value = pairs
        val coin = pairs.replace("-USD", "")
        viewModelScope.launch {
            val spotPrice = coinbaseRepository.getSpotPriceDB(coin)
            _currentPrice.value = formatCurrency(spotPrice)
        }
        getHistoricPrice(pairs, _selectedDate.value!!)
        Log.i(TAG, "Selected Pairs: ${_currencyPair.value}")
    }
    fun onDeleteTable() {
        viewModelScope.launch {
            coinbaseRepository.deleteTable()
        }
    }

    fun calculateTheGainzPercent(current: Double?, historic: Double?) {
        val difference = current?.minus(historic!!)
        val gainzIntPercent = (difference?.div(historic!!))?.times(100)
        val gainzString = String.format("%.2f", gainzIntPercent)
        if (difference!! > 0) {
            _theGainzPercent.value = "+$gainzString"
        } else {
            _theGainzPercent.value = gainzString
        }
        Log.i(TAG, "Gainz Percent: $gainzString")
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
        setSpotPrice()
        viewModelScope.launch {
            val refresh = async { refreshGainz() }
            refresh.await()
        }
        val current = unformatCurrency(_currentPrice.value!!)
        val historic = unformatCurrency(_historicPrice.value!!)
        calculateTheGainzPercent(current, historic)
        Log.i(TAG, "Pick Date: $current $historic")
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
        newInvestment.historicPrice = unformatCurrency(historicPrice.value!!)
        Log.i(TAG, "gatherInvestmentData: $newInvestment")
        return newInvestment
    }

    fun onSetInvestedAmount(cost: Double?) {
        _investedPrice.value = cost
        val currencyGainz = cost?.let { (_theGainzPercent.value?.toDouble()?.div(100))?.times(it) }
        if (cost == null) {
            _theGainzCurrency.value = 0.00
            Log.i(TAG, "NULL SetInvested Amount: ${_investedPrice.value}")
        } else {
            // Add format with commas. Change to strings?
            _theGainzCurrency.value = currencyGainz
            Log.i(TAG, "SetInvested Amount: ${_investedPrice.value}")
        }
    }

    fun getHistoricPrice(pair: String, date: String) {
        viewModelScope.launch {
            val historicPrice = coinbaseRepository.getHistoricPrice(pair, date)
            _historicPrice.value = formatCurrency(historicPrice)
        }
    }

    private fun getSpotPriceOf() {
        viewModelScope.launch {
            val spot = coinbaseRepository.getSpotPrice(_currencyPair.value!!)
            _currentPrice.value = formatCurrency(spot)
        }
    }

    fun getAllSpotPrices(pairs: String) {
        viewModelScope.launch {
            val spotPrice = coinbaseRepository.getAllSpotPrices(pairs)
            allCurrentPrices[pairs] = spotPrice
            Log.i(TAG, "MAP TEST: $allCurrentPrices")
        }
    }

    private fun setSpotPrice() {
        val pair = _currencyPair.value
        _currentPrice.value = allCurrentPrices[pair]?.let { formatCurrency(it) }
    }
}