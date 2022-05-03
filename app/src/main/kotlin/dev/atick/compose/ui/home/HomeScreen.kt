package dev.atick.compose.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.data.LineDataSet
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.home.components.EcgCard
import dev.atick.compose.ui.home.components.HrCard

@Composable
fun HomeScreen(
    onExitClick: () -> Unit,
    viewModel: BleViewModel = viewModel()
) {
    val averageHeartRate by viewModel.averageHeartRate.observeAsState(0.0F)
    val rrInterval by viewModel.rrInterval.observeAsState(initial = 0)
    val ecgDataset by viewModel.ecgDataset.observeAsState(
        LineDataSet(listOf(), "ECG")
    )

    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = "Dashboard",
            onExitClick = onExitClick
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

            Spacer(modifier = Modifier.height(16.dp))

            EcgCard(ecgDataset = ecgDataset)
        }
    }
}