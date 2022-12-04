package dev.atick.compose.utils

import dev.atick.movesense.data.ConnectionState
import dev.atick.network.utils.NetworkState

fun getNotificationTitle(
    connectionStatus: ConnectionState,
    networkState: NetworkState
): String {
    val sb = StringBuilder()
    sb.append("Movesense ")
    when (connectionStatus) {
        ConnectionState.NOT_CONNECTED -> sb.append("X")
        ConnectionState.CONNECTING -> sb.append("⦾")
        ConnectionState.CONNECTED -> sb.append("✓")
        ConnectionState.CONNECTION_FAILED -> sb.append("!")
        ConnectionState.DISCONNECTED -> sb.append("X")
    }
    sb.append(" | Network ")
    when (networkState) {
        NetworkState.CONNECTED -> sb.append("✓")
        NetworkState.LOSING -> sb.append("⦾")
        NetworkState.LOST -> sb.append("!")
        NetworkState.UNAVAILABLE -> sb.append("X")
    }
    return sb.toString()
}