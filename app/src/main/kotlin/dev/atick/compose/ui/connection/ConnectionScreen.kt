package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.connection.components.DeviceList

@Composable
@ExperimentalMaterialApi
@SuppressLint("MissingPermission")
fun ConnectionScreen(
    viewModel: BleViewModel = viewModel()
) {
    val isScanning by viewModel.isScanning
    val devices = viewModel.devices

    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(
                title = "Paired Devices",
                onSearchClick = {},
                onMenuClick = {}
            )

            DeviceList(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
                deviceList = devices,
                onDeviceClick = { device ->

                },
                onScanToggle = { viewModel.toggleScan() },
                isScanning = isScanning
            )
        }
    }
}