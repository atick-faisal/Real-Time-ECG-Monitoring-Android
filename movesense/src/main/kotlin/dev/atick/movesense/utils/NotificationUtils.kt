package dev.atick.movesense.utils

import dev.atick.movesense.data.ConnectionStatus
import dev.atick.network.utils.NetworkState

fun getNotificationTitle(
    connectionStatus: ConnectionStatus,
    networkState: NetworkState
): String {
    val sb = StringBuilder()
    sb.append("Movesense ")
    when (connectionStatus) {
        ConnectionStatus.NOT_CONNECTED -> sb.append("X")
        ConnectionStatus.CONNECTING -> sb.append("⦾")
        ConnectionStatus.CONNECTED -> sb.append("✓")
        ConnectionStatus.CONNECTION_FAILED -> sb.append("!")
        ConnectionStatus.DISCONNECTED -> sb.append("X")
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