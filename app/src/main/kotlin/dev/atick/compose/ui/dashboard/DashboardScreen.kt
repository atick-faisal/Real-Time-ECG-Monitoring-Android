package dev.atick.compose.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.dashboard.components.AbnormalEcgHeaderCard
import dev.atick.compose.ui.dashboard.components.EcgCard
import dev.atick.compose.ui.dashboard.components.HeartRateCard

@Composable
fun DashboardScreen(
    onExitClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    return Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBar(
                title = "Dashboard",
                onExitClick = onExitClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(64.dp),
                onClick = { /*TODO*/ }
            ) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = "Add Doctor")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    HeartRateCard(
                        Modifier.fillMaxWidth(),
                        heartRate = uiState.heartRate
                    )

                    EcgCard(
                        Modifier.fillMaxWidth(),
                        title = "Live ECG Signal",
                        ecgPlotData = uiState.ecgPlotData,
                        recordingState = uiState.recordingState
                    ) {

                    }

                    AbnormalEcgHeaderCard(Modifier.fillMaxWidth())
                }
            }

            items(uiState.abnormalEcgPlotData) { ecgPlotData ->
                EcgCard(
                    Modifier.fillMaxWidth(),
                    title = "Live ECG Signal",
                    ecgPlotData = ecgPlotData,
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
