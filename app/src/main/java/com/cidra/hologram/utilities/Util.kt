package com.cidra.hologram.utilities

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * 時刻切り捨て
 */
fun truncate(datetime: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = datetime

    return GregorianCalendar(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DATE)
    ).time
}

/**
 * 日付フォーマット
 * yyyy-MM-dd
 */
fun sdf(date: String): Date {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.parse(date)
}

/**
 * 昨日の日付
 */
fun yesterday(): Date {
    val today = Date()
    val cal = Calendar.getInstance()
    cal.time = today
    cal.add(Calendar.DAY_OF_MONTH, -1)
    return cal.time
}

fun dateAgo(amount: Int): Date {
    val today = Date()
    val cal = Calendar.getInstance()
    cal.time = today
    cal.add(Calendar.DAY_OF_MONTH, amount)
    Log.i("date", cal.time.toString())
    return cal.time
}

/**
 * 明日の日付
 */
fun tomorrow(): Date {
    val today = Date()
    val cal = Calendar.getInstance()
    cal.time = today
    cal.add(Calendar.DAY_OF_MONTH, 1)
    return cal.time
}

