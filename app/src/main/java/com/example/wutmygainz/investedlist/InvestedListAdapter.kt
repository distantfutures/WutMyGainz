package com.example.wutmygainz.investedlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.databinding.InvestedListItemBinding

class InvestedListAdapter: androidx.recyclerview.widget.ListAdapter<Investments, InvestedListAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(private var binding: InvestedListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Investments
        ) {
            // Binds Investments Data Class to Data Binding in RecyclerView
            binding.investments = item
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
        holder.bind(item)
    }
}