package dev.atick.compose.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.common.components.TopBar
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.home.components.HrCard

@Composable
fun HomeScreen(
    onDisconnect: () -> Unit,
    viewModel: BleViewModel = viewModel()
) {
    val averageHeartRate by viewModel.averageHeartRate.observeAsState(0.0F)
    val rrInterval by viewModel.rrInterval.observeAsState(initial = 0)

    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = "Dashboard",
            onExitClick = {
                viewModel.disconnect()
                onDisconnect.invoke()
            }
        )

        Column(
            modifier = Modifier
                .padding(
                    top = 80.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            HrCard(
                averageHeartRate = averageHeartRate,
                rrInterval = rrInterval
            )
        }
    }
}