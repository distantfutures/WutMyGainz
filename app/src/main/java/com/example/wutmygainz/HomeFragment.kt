package com.example.wutmygainz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.databinding.FragmentHomeBinding
import com.example.wutmygainz.network.CoinbaseApi
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        val currencyPairs = resources.getStringArray(R.array.currency_pairs)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_currency_pair, currencyPairs)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.viewModelHome = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }
}
