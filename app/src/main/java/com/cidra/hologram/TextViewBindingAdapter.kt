package com.cidra.hologram

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.github.curioustechizen.ago.RelativeTimeTextView
import java.text.SimpleDateFormat
import java.util.*


@BindingMethods(
    value = [
        BindingMethod(
            type = RelativeTimeTextView::class,
            attribute = "relative_time_prefix",
            method ="setPrefix"
        ),
        BindingMethod(
            type = RelativeTimeTextView::class,
            attribute = "relative_time_suffix",
            method = "setSuffix"
        )
    ]
)


object TextViewBindingAdapter

@BindingAdapter("reference_time")
fun relativeTimeText(tvRelative: RelativeTimeTextView, time: String?) {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateObject = sdf.parse(time).time

    val now = Date().time
    val differenceTime = now - dateObject

    tvRelative.setReferenceTime(now - differenceTime)

}