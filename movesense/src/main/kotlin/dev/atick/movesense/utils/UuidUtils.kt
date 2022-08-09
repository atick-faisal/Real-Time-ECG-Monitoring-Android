package dev.atick.ble.utils

import java.util.*

fun UUID.toShortString(): String {
    return toString().substring(0, 8)
}

fun UUID.getName(): String {
    return when (this.toString().lowercase()) {
        "00000000-78fc-48fe-8e23-433b3a1942d0" -> "Music Service"
        "00010000-78fc-48fe-8e23-433b3a1942d0" -> "Navigation Service"
        "00030000-78fc-48fe-8e23-433b3a1942d0" -> "Motion Service"
        "00040000-78fc-48fe-8e23-433b3a1942d0" -> "Weather Service"
        "0000180d-0000-1000-8000-00805f9b34fb" -> "Heart Rate Service"
        "0000180f-0000-1000-8000-00805f9b34fb" -> "Battery Service"
        else -> "Unknown Service"
    }
}