package dev.atick.compose.ui.dashboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
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

    val context = LocalContext.current
    val scanner = remember { BarcodeScanning.getClient() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val image = InputImage.fromBitmap(it, 0)
            scanner.process(image).addOnSuccessListener { qrCodes ->
                val qrCode = qrCodes.firstOrNull()
                qrCode?.let { code ->
                    code.rawValue?.let { doctor ->
                        val (name, id) = doctor.split(",")
                        Toast.makeText(
                            context,
                            "Connecting to Dr. : $name",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.connectDoctor(id)
                    } ?: Toast.makeText(
                        context,
                        "QR Scan Failed!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    return Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
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
                onClick = { launcher.launch(null) }
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
                        viewModel.record()
                    }

                    AbnormalEcgHeaderCard(Modifier.fillMaxWidth())
                }
            }

            items(uiState.abnormalEcgPlotData) { ecgPlotData ->
                EcgCard(
                    Modifier.fillMaxWidth(),
                    title = ecgPlotData.getTimestamp(),
                    ecgPlotData = ecgPlotData,
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}