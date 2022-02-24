package com.example.wutmygainz

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wutmygainz.database.Investments
import com.example.wutmygainz.investedlist.InvestedListAdapter
import com.google.android.material.textview.MaterialTextView
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

@BindingAdapter(value = ["investments", "currentPrice"])
fun MaterialTextView.setDataText(investments: Investments?, currentPrice: String?) {
    investments?.let {
        text = formatInvestments(investments, currentPrice, context.resources)
    }
}

@BindingAdapter(value = ["currentPrice","historicPrice"])
fun MaterialTextView.setGainzText(currentPrice: String?, historic: Double?) {
    // Problem not passing in currentPrice. Maybe get destroyed upon swipe
    currentPrice?.let {
        Log.i("CheckBindingAdapter", "$currentPrice - $historic")
        var gainzPercent = calculateGainzPercent(currentPrice, historic!!)
        text = formatPercent(gainzPercent.first, gainzPercent.second)
    }
}