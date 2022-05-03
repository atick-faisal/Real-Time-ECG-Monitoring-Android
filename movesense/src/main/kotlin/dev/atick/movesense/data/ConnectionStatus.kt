package dev.atick.movesense.data

enum class ConnectionStatus(val description: String) {
    NOT_CONNECTED("NOT CONNECTED"),
    CONNECTING("CONNECTING ... "),
    CONNECTED("CONNECTED"),
    CONNECTION_FAILED("CONNECTION FAILED!"),
    DISCONNECTED("DISCONNECTED")
}