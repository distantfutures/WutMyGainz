package com.example.wutmygainz.investedlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.databinding.InvestedListItemBinding
import com.example.wutmygainz.home.HomeViewModel

class InvestedListAdapter(private val homeVM: HomeViewModel): androidx.recyclerview.widget.ListAdapter<Investments, InvestedListAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(private var binding: InvestedListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Investments,
            homeViewModel: HomeViewModel
        ) {
            // Binds Investments Data Class to Data Binding in RecyclerView
            binding.investments = item
            binding.viewModel = homeViewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Investments>() {
        override fun areItemsTheSame(oldItem: Investments, newItem: Investments): Boolean {
            return oldItem.investmentId == newItem.investmentId
        }

        override fun areContentsTheSame(oldItem: Investments, newItem: Investments): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(InvestedListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item, homeVM)
    }
}