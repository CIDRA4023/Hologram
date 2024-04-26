package com.cidra.hologram.utilities

import android.util.Log
import java.text.ParseException
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

    Log.e("sdf", date)
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    val parsedDate = sdf.parse(date)
    if (date != sdf.format(parsedDate)) {
        // フォーマットが一致しない場合の処理をここに記述します
        // 例えば、エラーメッセージを表示するなど
        throw ParseException("Invalid date format", 0)
    }
    return parsedDate

//    try {
//
//    } catch (e: ParseException) {
//        e.printStackTrace()
//        throw ParseException("parse error", 0)
//    }
//    Log.e("sdf", date)
////    if(date == "null") return  Date()
//    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
//    return sdf.parse(date)
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

