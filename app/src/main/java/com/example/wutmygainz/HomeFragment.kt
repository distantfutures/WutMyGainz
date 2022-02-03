package com.example.wutmygainz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.wutmygainz.databinding.FragmentHomeBinding
import com.example.wutmygainz.network.CoinbaseApi
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val job = Job()
        val coroutineScope = CoroutineScope(job + Dispatchers.Main)

        val date = "2014-10-13"
        val currencyPair = "BTC-USD"

        coroutineScope.launch {
            val responseHistoric = CoinbaseApi.retrofitService.getHistoricCoinPrice(currencyPair, date)
            val responseSpot = CoinbaseApi.retrofitService.getCurrentCoinPrice(currencyPair)
            if (responseHistoric.isSuccessful) {
                Log.i("CheckAPI Service", "Historic: ${responseHistoric.body()?.data?.amount}")
                Log.i("CheckAPI Service", "Historic: ${responseSpot.body()?.data?.amount}")

            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
        return binding.root
    }
}
