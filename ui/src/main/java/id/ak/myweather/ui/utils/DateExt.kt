package id.ak.myweather.ui.utils

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
fun Long.formatAsString(pattern: String, isInMillisecond: Boolean = false): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val instant = if (isInMillisecond) Instant.ofEpochMilli(this) else Instant.ofEpochSecond(this)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return dateTime.format(formatter)
}

fun Long.isInSameDayAs(other: Long, isInMillisecond: Boolean = false): Boolean {
    val thisDate =
        (if (isInMillisecond) Instant.ofEpochMilli(this) else Instant.ofEpochSecond(this))
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    val otherDate =
        (if (isInMillisecond) Instant.ofEpochMilli(other) else Instant.ofEpochSecond(other))
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    return thisDate.isEqual(otherDate)
}

fun Long.isToday(isInMillisecond: Boolean = false): Boolean {
    val thisDate = LocalDateTime.ofInstant(
        (if (isInMillisecond) Instant.ofEpochMilli(this) else Instant.ofEpochSecond(this)),
        ZoneId.systemDefault()
    ).toLocalDate()
    val today = LocalDate.now()
    return thisDate.isEqual(today)
}