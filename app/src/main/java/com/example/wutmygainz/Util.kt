package com.example.wutmygainz

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import com.example.wutmygainz.database.Investments
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun formatInvestments(investments: Investments, currentPrice: String?, resources: Resources): Spanned? {
    val sb = StringBuilder()
    sb.apply {
        investments.let {
            append("<b>${resources.getString(R.string.purchase_date_text)}</b> ")
            append(formatLongDate(it))
            append("<br><b>${resources.getString(R.string.historic_price_text)}:</b> ")
            append(formatCurrency(it.historicPrice))
            append("<br><b>${resources.getString(R.string.invested_amount_text)}</b> ")
            append(formatCurrency(it.investedPrice))
            append("<br><b>${resources.getString(R.string.current_value_text)}</b> ")
            val currentValue = currentValue(currentPrice, it.historicPrice, it.investedPrice)
            append(formatCurrency(currentValue!!))
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

fun formatLongDate(date: Investments): String {
    val dateLong = Date(date.purchaseDate)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(dateLong)
}

fun formatCurrency(investedPrice: Double): String {
    val decimalFormat = DecimalFormat("$#,###,##0.00")
    return decimalFormat.format(investedPrice)
}

fun currentValue(currentPrice: String?, historicPrice: Double, investedPrice: Double?): Double? {
    var gainzPercent = calculateGainzPercent(currentPrice, historicPrice)
    val difference = investedPrice?.let { (gainzPercent.first.div(100)).times(investedPrice) }
    val currencyGainz = difference?.let { investedPrice.plus(it) }
    return if (investedPrice == null) {
        0.00
    } else {
        currencyGainz
    }
}

fun calculateGainzPercent(current: String?, historic: Double?): Pair<Double, Double> {
    val difference = unformatCurrency(current!!).minus(historic!!)
    val gainzIntPercent = (difference.div(historic)).times(100)
    Log.i("CheckViewModel", "Gainz List Percent: $gainzIntPercent Difference: $difference")
    return Pair (gainzIntPercent, difference)
}
fun formatPercent(gainzPercent: Double?, difference: Double?): String {
    return if (difference!! > 0) {
        String.format("%%%+.2f", gainzPercent)
    } else {
        String.format("%%%.2f", gainzPercent)
    }
}
private fun unformatCurrency(price: String): Double {
    val currencyAsDouble = price.replace(",", "")
    return currencyAsDouble.toDouble()
}