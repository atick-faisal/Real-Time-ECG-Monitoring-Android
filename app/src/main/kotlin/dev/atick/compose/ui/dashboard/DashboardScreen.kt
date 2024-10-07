package dev.atick.compose.ui.dashboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.orhanobut.logger.Logger
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.dashboard.components.AbnormalEcgHeaderCard
import dev.atick.compose.ui.dashboard.components.AudioRecordDialog
import dev.atick.compose.ui.dashboard.components.EcgCard
import dev.atick.compose.ui.dashboard.components.HeartRateCard
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onExitClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scanner = remember { BarcodeScanning.getClient() }

    var showRecordingDialog by remember { mutableStateOf(false) }

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
                            "Connecting to Dr. $name",
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
                onScanClick = { launcher.launch(null) },
                onExitClick = onExitClick,
                onLogoutClick = {
                    coroutineScope.launch {
                        viewModel.logout()
                        onLogoutClick.invoke()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(64.dp),
                onClick = { showRecordingDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = "Record Audio")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
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
                    )

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

        AnimatedVisibility(visible = showRecordingDialog) {
            AlertDialog(
                onDismissRequest = {},
                text = {
                    AudioRecordDialog(context = context) { file ->
                        Logger.i("AUDIO FILE: $file")
                        viewModel.pushAudio(file)
                    }
                },
                confirmButton = {
                    Button(onClick = { showRecordingDialog = false }) {
                        Text(text = "Done")
                    }
                }
            )
        }
    }
}
