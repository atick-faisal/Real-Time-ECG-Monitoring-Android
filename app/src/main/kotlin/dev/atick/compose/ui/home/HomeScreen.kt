package dev.atick.compose.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.dashboard.data.EcgPlotData
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

    val rPeakDataset by viewModel.rPeakDataset.observeAsState(
        ScatterDataSet(listOf(), "R_PEAK")
    )

    val scanner = remember { BarcodeScanning.getClient() }

    val context = LocalContext.current
    val recordState by viewModel.recordState
    val abnormalEcgList by viewModel.abnormalEcgList.collectAsState(listOf())
    val scaffoldState = rememberScaffoldState()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val image = InputImage.fromBitmap(it, 0)
            scanner.process(image).addOnSuccessListener { qrCodes ->
                val qrCode = qrCodes.firstOrNull()
                qrCode?.let { code ->
                    code.rawValue?.let { doctorId ->
                        Toast.makeText(
                            context,
                            "Connecting to Doctor ID: $doctorId",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.connectDoctor(doctorId)
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
        topBar = {
            TopBar(
                title = "Dashboard",
                onExitClick = onExitClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { launcher.launch(null) }) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Scan QR"
                )
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HrCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                averageHeartRate = averageHeartRate,
                rrInterval = rrInterval
            )

            Spacer(modifier = Modifier.height(16.dp))

            EcgCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = "Live ECG Signal",
                data = EcgPlotData(
                    id = 0L,
                    ecg = ecgDataset,
                    rPeaks = rPeakDataset
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { viewModel.record() }
            ) {
                Text(text = recordState.description)
            }

            Spacer(modifier = Modifier.height(16.dp))

            abnormalEcgList.forEach { ecgPlotData ->
                EcgCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = ecgPlotData.getTimestamp(),
                    data = ecgPlotData
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
