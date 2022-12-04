package dev.atick.compose.ui.connection.data

enum class ConnectionUiState(val description: String) {
    SCANNING("Looking for Movesense Device ..."),
    DEVICE_FOUND("Found a Movesense Device!"),
    SCAN_FAILED("Could Not Find Movesense Device"),
    CONNECTING("Establishing Connection ... "),
    CONNECTED("Connected Successfully!"),
    CONNECTION_FAILED("Connection Failed. Try Again")
}