package dev.atick.movesense.data

data class EcgSignal(
    val timestamp: Long,
    val values: List<Int>
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
