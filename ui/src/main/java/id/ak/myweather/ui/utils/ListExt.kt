package id.ak.myweather.ui.utils

import kotlin.math.ceil

fun <T : Any> List<T>.getMiddleItem(): T {
    val middleIndex = ceil(size.toDouble() / 2.0).toInt()
    return this[middleIndex]
}