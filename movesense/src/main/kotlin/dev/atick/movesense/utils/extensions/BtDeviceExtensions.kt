package dev.atick.movesense.utils.extensions

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass.Device.Major.*
import android.bluetooth.BluetoothDevice
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.ui.graphics.vector.ImageVector

@SuppressLint("MissingPermission")
fun BluetoothDevice.getDeviceIcon(): ImageVector {
    return when (this.bluetoothClass.majorDeviceClass) {
        AUDIO_VIDEO -> Icons.Default.BluetoothAudio
//        COMPUTER -> Icons.Default.Computer
        HEALTH -> Icons.Default.DirectionsRun
        IMAGING -> Icons.Default.Image
        NETWORKING -> Icons.Default.Cloud
        PERIPHERAL -> Icons.Default.Image
        PHONE -> Icons.Default.KeyboardAlt
        TOY -> Icons.Default.Toys
        UNCATEGORIZED -> Icons.Default.Bluetooth
        WEARABLE -> Icons.Default.Watch
        else -> Icons.Outlined.ViewInAr
    }
}