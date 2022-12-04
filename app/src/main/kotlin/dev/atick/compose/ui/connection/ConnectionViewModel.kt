package dev.atick.compose.ui.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.ui.connection.data.ConnectionUiState
import dev.atick.movesense.Movesense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val movesense: Movesense
) : ViewModel() {

    lateinit var movesenseMacAddress: String
    val connectionStatus = movesense.connectionState

    private val _uiState = MutableStateFlow(ConnectionUiState.SCAN_FAILED)
    val uiState: StateFlow<ConnectionUiState> get() = _uiState

    init {
        scan()
    }

    fun scan() {
        viewModelScope.launch {
            _uiState.update { ConnectionUiState.SCANNING }
            movesense.getMovesenseDeviceAddress()?.let { macAddress ->
                movesenseMacAddress = macAddress
                _uiState.update { ConnectionUiState.DEVICE_FOUND }
            } ?: _uiState.update { ConnectionUiState.SCAN_FAILED }
        }
    }

    fun setConnecting() {
        _uiState.update { ConnectionUiState.CONNECTING }
    }

    fun setConnected() {
        _uiState.update { ConnectionUiState.CONNECTED }
    }

    fun setConnectionFailed() {
        _uiState.update { ConnectionUiState.CONNECTION_FAILED }
    }
}