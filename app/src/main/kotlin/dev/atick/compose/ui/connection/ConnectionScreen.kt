package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = "Paired Devices",
            onSearchClick = {},
            onMenuClick = {}
        )

        DeviceList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 64.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
            deviceList = devices,
            onDeviceClick = { address ->
                viewModel.connect(address)
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(visible = isScanning) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { viewModel.toggleScan() }
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
                    onClick = { viewModel.toggleScan() }
                ) {
                    Text(text = "Start Scan")
                }
            }
        }
    }
}