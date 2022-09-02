package dev.atick.core.utils.extensions

fun <T : Comparable<T>> Iterable<T>.argmax(): Int {
    return withIndex().maxByOrNull { it.value }?.index ?: 0
}