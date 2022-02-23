package com.example.wutmygainz

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.investedlist.InvestedListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val adapterScope = CoroutineScope(Dispatchers.Default)

// Creates attribute in RecyclerView layout to take List of Investments
@BindingAdapter("listData")
fun bindRecyclerView(recycler: RecyclerView, data: List<Investments>?) {
    adapterScope.launch {
        val adapter = recycler.adapter as InvestedListAdapter
        withContext(Dispatchers.Main) {
            adapter.submitList(data)
        }
    }
}