package dev.atick.compose.ui.connection.components

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun BluetoothDevice(
    modifier: Modifier = Modifier,
    bluetoothDevice: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit
) {
    return Card(
        modifier = modifier.then(
            Modifier.fillMaxWidth().clickable {
                onClick.invoke(bluetoothDevice)
            }
        ),
        elevation = if (isSystemInDarkTheme()) 0.dp else 2.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        onClick = { onClick.invoke(bluetoothDevice) }
    ) {
        DeviceInfo(
            bluetoothDevice = bluetoothDevice,
            isConnected = false
        )
    }
}