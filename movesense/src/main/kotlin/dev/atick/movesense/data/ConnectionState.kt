package dev.atick.movesense.data

import androidx.annotation.StringRes
import dev.atick.movesense.R

enum class ConnectionState(@StringRes val description: Int) {
    NOT_CONNECTED(R.string.movesense_not_connected),
    CONNECTING(R.string.movesense_connecting),
    CONNECTED(R.string.movesense_connected),
    CONNECTION_FAILED(R.string.movesense_connection_failed),
    DISCONNECTED(R.string.movesense_disconnected)
}