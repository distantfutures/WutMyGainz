package com.example.wutmygainz.home

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.R
import com.example.wutmygainz.database.InvestmentsDatabase
import com.example.wutmygainz.database.InvestmentsDatabaseDAO
import com.example.wutmygainz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

//    private val homeViewModel: HomeViewModel by lazy {
//        ViewModelProvider(this).get(HomeViewModel::class.java)
//    }
    private lateinit var homeViewModel: HomeViewModel

    override fun onResume() {
        super.onResume()
        val currencyPairs = resources.getStringArray(R.array.currency_pairs)
        val arrayAdapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_currency_pair, currencyPairs)
        binding.currencyPairsTextView.setAdapter(arrayAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val application = requireNotNull(this.activity).application

        val datasource = InvestmentsDatabase.getInstance(application).investmentDatabaseDao

        val viewModelFactory = HomeViewModelFactory(datasource)

        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        binding.viewModelHome = homeViewModel

        binding.fragmentHome = this

        binding.lifecycleOwner = this

        binding.investedCostInputText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val investedCostLong = binding.investedCostInputText.text.toString().toDoubleOrNull()
                    homeViewModel.onSetInvestedAmount(investedCostLong)
                    false
                }
                else -> { true }
            }
        }

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
    