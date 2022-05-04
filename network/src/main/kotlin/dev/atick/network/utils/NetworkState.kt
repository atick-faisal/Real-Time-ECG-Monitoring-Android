package dev.atick.network.utils

enum class NetworkState(val description: String) {
    CONNECTED("CONNECTED"),
    LOSING("LOSING CONNECTION"),
    LOST("CONNECTION LOST"),
    UNAVAILABLE("NETWORK UNAVAILABLE")
}