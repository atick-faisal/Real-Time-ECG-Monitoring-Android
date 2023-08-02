package dev.atick.compose.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AbnormalEcgHeaderCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Anomalies",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "vBeat",
                    tint = Color(0xFF8429F6)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "V-BEAT")
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "vBeat",
                    tint = Color(0xFFf44336)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "S-BEAT")
                Spacer(modifier = Modifier.width(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.Rectangle,
                    contentDescription = "AF",
                    tint = Color(0xFF_FFEBEE)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ATRIAL FIBRILLATION")
            }
        }
    }
}