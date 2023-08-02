package dev.atick.compose.ui.dashboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atick.compose.R
import dev.atick.compose.ui.dashboard.data.EcgPlotData
import dev.atick.compose.ui.dashboard.data.RecordingState

@Composable
fun EcgCard(
    modifier: Modifier = Modifier,
    title: String,
    ecgPlotData: EcgPlotData,
    recordingState: RecordingState = RecordingState.NotRecording,
    onRecordClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (ecgPlotData.af == 1) MaterialTheme.colors.error
        else MaterialTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6F),
                    painter = painterResource(id = R.drawable.ecg_background),
                    contentDescription = "ECG Background",
                    contentScale = ContentScale.FillBounds
                )
                EcgPlot(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6F)
                        .padding(16.dp),
                    data = ecgPlotData
                )
            }

            onRecordClick?.let {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    onClick = onRecordClick
                ) {
                    Text(text = recordingState.description)
                }
            }
        }
    }
}