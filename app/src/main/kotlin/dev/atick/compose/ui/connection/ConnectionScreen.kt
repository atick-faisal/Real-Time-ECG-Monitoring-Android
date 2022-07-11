package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.R
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.connection.components.DeviceList

@Composable
@ExperimentalMaterialApi
@SuppressLint("MissingPermission")
fun ConnectionScreen(
    onDeviceClick: (String) -> Unit,
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
            title = "Available Devices",
            onSearchClick = {},
            onMenuClick = {}
        )

        AnimatedVisibility(
            visible = devices.size > 0
        ) {
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
                onDeviceClick = onDeviceClick
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = devices.size == 0
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_empty),
                    contentDescription = "No Device Found"
                )
                Text(
                    text = "No Device Available",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Please Scan for Devices",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 14.sp
                )
            }
        }

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
                    onClick = { viewModel.stopScan() }
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
                    onClick = { viewModel.startScan() }
                ) {
                    Text(text = "Start Scan")
                }
            }
        }
    }
}