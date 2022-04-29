package dev.atick.compose.ui.connection.components

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
import dev.atick.movesense.data.BtDevice

@ExperimentalMaterialApi
@Composable
fun BtDeviceCard(
    modifier: Modifier = Modifier,
    btDevice: BtDevice,
    onClick: (String) -> Unit
) {
    return Card(
        modifier = modifier.then(
            Modifier.fillMaxWidth().clickable {
                onClick.invoke(btDevice.address)
            }
        ),
        elevation = if (isSystemInDarkTheme()) 0.dp else 2.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        onClick = { onClick.invoke(btDevice.address) }
    ) {
        DeviceInfo(
            btDevice = btDevice,
            isConnected = false
        )
    }
}