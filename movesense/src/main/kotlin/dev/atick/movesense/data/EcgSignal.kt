package dev.atick.movesense.data

import java.util.*

data class EcgSignal(
    val values: List<Int>,
    val timestamp: Long = Date().time
) {
    override fun toString(): String {
        return "$timestamp,${values.joinToString(separator = ",")}"
    }
}

fun MutableList<EcgSignal>.toCsv(): String {
    val sb = StringBuilder()
    sb.append(
        "timestamp,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15\n"
    )
    forEach {
        sb.append(it.toString())
        sb.append("\n")
    }

    return sb.toString()
}
