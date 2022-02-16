package com.example.wutmygainz

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        val currencyPairs = resources.getStringArray(R.array.currency_pairs)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_currency_pair, currencyPairs)
        binding.currencyPairsTextView.setAdapter(arrayAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.viewModelHome = homeViewModel

        binding.fragmentHome = this

        binding.lifecycleOwner = this

        //binding.datePicker.setOnClickListener{datePickerDialog()}

        binding.currencyPairsTextView.setOnItemClickListener { parent, view, position, l ->
            val selectedPairs = parent.getItemAtPosition(position).toString()
            Log.i("CheckHomeFrag", "ClickTest $selectedPairs")
            homeViewModel.onSetSelectedPairs(selectedPairs)
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun datePickerDialog() {
        DatePickerDialog(requireActivity(),  android.R.style.Theme_Material_Dialog, { _, year, month, day ->
            homeViewModel.pickedDate(year, month, day)
            showToastLong(homeViewModel.selectedDate.value)
        }, homeViewModel.startYear, homeViewModel.startMonth, homeViewModel.startDay).show()
    }
    fun showToastLong(str: String?) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}
    