package com.example.wutmygainz.investedlist

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.wutmygainz.R
import com.example.wutmygainz.database.InvestmentsDatabase
import com.example.wutmygainz.databinding.FragmentInvestedListBinding
import com.example.wutmygainz.home.HomeViewModel
import com.example.wutmygainz.home.HomeViewModelFactory

class InvestedListFragment : Fragment() {

//    private val sharedViewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentInvestedListBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInvestedListBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        
        val viewModelFactory = HomeViewModelFactory(application)

        sharedViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this

        binding.viewModel = sharedViewModel

        binding.investedFragment = this

        // Initializes List Adapter
        val adapter = InvestedListAdapter(sharedViewModel)

        // Bind adapter to RecyclerView
        binding.investedListRecycler.adapter = adapter

        Log.i("CheckListFragment", "Current Price? ${sharedViewModel.theCurrentPrice}")
        return binding.root
    }
}