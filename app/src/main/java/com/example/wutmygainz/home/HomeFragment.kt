package com.example.wutmygainz.home

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.R
import com.example.wutmygainz.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text

private const val TAG = "HomeFragCheck"
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onDestroy() {
        Log.i("CheckHomeFragment", "Fragment Destroyed!")
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPause() {
        Log.i("CheckHomeFragment", "Fragment Paused!")
        Log.i("CheckHomeFragment", "Pre-Binding Test ${homeViewModel.theCurrentPrice}")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.i("CheckHomeFragment", "Fragment Destroy View!")
        super.onDestroyView()
    }

    override fun onStop() {
        Log.i("CheckHomeFragment", "Fragment Stopped!")
        super.onStop()
    }

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = HomeViewModelFactory(application)

        // Need to understand why requireActivity() works and "this" doesn't
        homeViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(HomeViewModel::class.java)

        binding.viewModelHome = homeViewModel

        binding.fragmentHome = this

        binding.lifecycleOwner = this

        homeViewModel.onDeleteTable()

        homeViewModel.isToday.observe(viewLifecycleOwner) {
            if (it) {
                homeViewModel.clearHistoricPrice()
            }
        }
        homeViewModel.selectedDate.observe(viewLifecycleOwner) {
            homeViewModel.setSpotPriceFromMap() //Needed here?
        }
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
        binding.investedCostInputText.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.addButton.isEnabled = s?.isNotEmpty() ?: false
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.currencyPairsTextView.setOnItemClickListener { parent, view, position, l ->
            val selectedPairs = parent.getItemAtPosition(position).toString()
            Log.i(TAG, "ClickTest $selectedPairs")
            homeViewModel.onSetPairs(selectedPairs)
        }
        getAllSpotPrices()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getAllSpotPrices() {
        val allCoinPairs = resources.getStringArray(R.array.currency_pairs)
        for (i in allCoinPairs.indices) {
            Log.i(TAG, "StringCheck: ${allCoinPairs[i]}")
            homeViewModel.getAllSpotPrices(allCoinPairs[i])
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun datePickerDialog() {
        DatePickerDialog(requireActivity(),  android.R.style.Theme_Material_Dialog, { _, year, month, day ->
            homeViewModel.pickedDate(year, month, day)
            val date = homeViewModel.selectedDate.value
            showToastLong(date)
            getAllSpotPrices()
            Log.i(TAG, "Day selected! $day")
        }, homeViewModel.startYear, homeViewModel.startMonth, homeViewModel.startDay).show()
    }
    private fun showToastLong(str: String?) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}
    