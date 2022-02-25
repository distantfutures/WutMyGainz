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

@BindingAdapter(value = ["investmentsData", "spotPriceMapData"])
fun MaterialTextView.setDataText(investments: Investments?, spotPriceMap: Map<String, Double>?) {
    investments?.let {
        val spotPrice = spotPriceMap?.get(investments.currencyPair)
        text = formatInvestments(investments, spotPrice, context.resources)
        Log.i("CheckBindingAdapter", "SPOT DATA: $spotPrice")
    }
}

@BindingAdapter(value = ["investments","spotPriceMap"])
fun MaterialTextView.setGainzText(investments: Investments?, spotPriceMap: Map<String, Double>?) {
    spotPriceMap?.let {
        val historicPrice = investments?.historicPrice
        val spotPrice = spotPriceMap.get(investments?.currencyPair)
        val gainzPercent = calculateGainzPercent(historicPrice, spotPrice)
        text = formatPercent(gainzPercent.first, gainzPercent.second)
        Log.i("CheckBindingAdapter", "SPOT: $spotPrice, HIS: $historicPrice, MAP: $spotPriceMap")
    }
}