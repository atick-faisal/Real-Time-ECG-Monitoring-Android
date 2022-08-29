package dev.atick.movesense.config

object MovesenseConfig {
    const val URI_EVENT_LISTENER = "suunto://MDS/EventListener"
    const val SCHEME_PREFIX = "suunto://"

    const val URI_ECG_INFO = "/Meas/ECG/Info"
    const val URI_ECG_ROOT = "/Meas/ECG/"
    const val URI_MEAS_HR = "/Meas/HR"

    const val DEFAULT_ECG_BUFFER_LEN = 16
    const val DEFAULT_ECG_SAMPLE_RATE = 128
    const val ECG_SEGMENT_LEN = (DEFAULT_ECG_SAMPLE_RATE * 5)
    const val NETWORK_UPDATE_CYCLE =
        (DEFAULT_ECG_SAMPLE_RATE / DEFAULT_ECG_BUFFER_LEN) * 5
}