package dev.atick.compose.ui.home.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
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
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        elevation = if (isSystemInDarkTheme()) 0.dp else 2.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface
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