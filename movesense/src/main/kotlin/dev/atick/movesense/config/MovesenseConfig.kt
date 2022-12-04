package dev.atick.movesense.config

object MovesenseConfig {
    const val SCAN_TIMEOUT = 10_000L

    const val URI_EVENT_LISTENER = "suunto://MDS/EventListener"
    const val URI_ECG_ROOT = "/Meas/ECG/"
    const val URI_MEAS_HR = "/Meas/HR"

    private const val DEFAULT_ECG_BUFFER_LEN = 16
    const val DEFAULT_ECG_SAMPLE_RATE = 128
    const val NETWORK_UPDATE_CYCLE =
        (DEFAULT_ECG_SAMPLE_RATE / DEFAULT_ECG_BUFFER_LEN) * 5
}