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

        coroutineScope.launch {
            val response = CoinbaseApi.retrofitService.getBTCPrice()
            if (response.isSuccessful) {
                Log.i("CheckAPI Service", "Success! ${response.body()}")
                val dataResponse = response.body()
                Log.i("CheckAPI Service", "Success! ${dataResponse?.data?.base}")
            } else {
                Log.i("CheckAPI Service", "Failed!")
            }
        }
        return binding.root
    }
}
