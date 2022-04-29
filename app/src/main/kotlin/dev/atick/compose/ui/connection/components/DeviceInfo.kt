package dev.atick.compose.ui.connection.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass.Device.Major.COMPUTER
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atick.movesense.utils.extensions.getDeviceIcon

@SuppressLint("MissingPermission")
@Composable
fun DeviceInfo(
    modifier: Modifier = Modifier,
    bluetoothDevice: BluetoothDevice,
    isConnected: Boolean
) {
    Row(
        modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = bluetoothDevice.getDeviceIcon(),
                contentDescription = "Device type",
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp),
                tint = if (
                    bluetoothDevice.bluetoothClass.majorDeviceClass == COMPUTER) {
                    if (isConnected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.primary
                } else MaterialTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Text(
                    text = bluetoothDevice.name,
                    color = if (isConnected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bluetoothDevice.address,
                    color = if (isConnected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.onSurface,
                    fontSize = 14.sp,
                )
            }
        }

        Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = "Connect",
            tint = if (isConnected) MaterialTheme.colors.onPrimary
            else MaterialTheme.colors.onSurface
        )
    }
}