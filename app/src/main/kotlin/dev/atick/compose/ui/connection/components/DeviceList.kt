package dev.atick.compose.ui.connection.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.atick.movesense.data.BtDevice

@Composable
@ExperimentalMaterialApi
fun DeviceList(
    deviceList: List<BtDevice>,
    onDeviceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        LazyColumn {
            items(deviceList) { device ->
                Spacer(modifier = Modifier.height(12.dp))
                BtDeviceCard(
                    btDevice = device,
                    onClick = onDeviceClick
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}