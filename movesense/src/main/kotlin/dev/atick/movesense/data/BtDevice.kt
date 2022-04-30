package dev.atick.movesense.data

import android.bluetooth.BluetoothClass
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ViewInAr
import com.google.gson.annotations.SerializedName

data class BtDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val type: Int
) {
    val icon = when (type) {
        BluetoothClass.Device.Major.AUDIO_VIDEO -> Icons.Default.BluetoothAudio
        BluetoothClass.Device.Major.HEALTH -> Icons.Default.DirectionsRun
        BluetoothClass.Device.Major.IMAGING -> Icons.Default.Image
        BluetoothClass.Device.Major.NETWORKING -> Icons.Default.Cloud
        BluetoothClass.Device.Major.PERIPHERAL -> Icons.Default.Image
        BluetoothClass.Device.Major.PHONE -> Icons.Default.KeyboardAlt
        BluetoothClass.Device.Major.TOY -> Icons.Default.Toys
        BluetoothClass.Device.Major.UNCATEGORIZED -> Icons.Default.Bluetooth
        BluetoothClass.Device.Major.WEARABLE -> Icons.Default.Watch
        else -> Icons.Outlined.ViewInAr
    }

    val movesense = name.contains("movesense", true)
}
