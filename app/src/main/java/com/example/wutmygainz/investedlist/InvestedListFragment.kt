package com.example.wutmygainz.investedlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var sharedViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentInvestedListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invested_list, container, false)

        val application = requireNotNull(this.activity).application

        val datasource = InvestmentsDatabase.getInstance(application).investmentDatabaseDao

        val viewModelFactory = HomeViewModelFactory(datasource)

        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this

        binding.viewModel = sharedViewModel

        binding.investedFragment = this

        // Initializes List Adapter
        val adapter = InvestedListAdapter()

        // Bind adapter to RecyclerView
        binding.investedListRecycler.adapter = adapter

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
}