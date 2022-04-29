package dev.atick.compose.ui.connection.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterialApi
@SuppressLint("MissingPermission")
fun DeviceList(
    deviceList: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onScanToggle: () -> Unit,
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(deviceList) { device ->
                BluetoothDevice(
                    bluetoothDevice = device,
                    onClick = onDeviceClick
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = isScanning) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { onScanToggle() }
            ) {
                Text(text = "Stop Scan")
            }
        }

        AnimatedVisibility(visible = !isScanning) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { onScanToggle() }
            ) {
                Text(text = "Start Scan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}