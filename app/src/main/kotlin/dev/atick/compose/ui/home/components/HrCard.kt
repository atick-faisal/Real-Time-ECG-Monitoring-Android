package dev.atick.compose.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HrCard(
    averageHeartRate: Float,
    rrInterval: Int,
    modifier: Modifier = Modifier
) {
    return Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "HEART RATE")
            Text(
                text = "%.2f BPM".format(averageHeartRate),
                fontSize = 32.sp,
                fontWeight = FontWeight.Thin
            )
            Text(text = "RR INTERVAL: $rrInterval ms")
        }
    }
}