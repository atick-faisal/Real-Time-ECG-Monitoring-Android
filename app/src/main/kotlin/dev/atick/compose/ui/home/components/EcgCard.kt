package dev.atick.compose.ui.home.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.atick.compose.ui.dashboard.data.EcgPlotData

@Composable
fun EcgCard(
    title: String,
    data: EcgPlotData,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title)
//            LinePlot(
//                dataset1 = ecgDataset,
//                dataset2 = rPeakDataset,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio((16.0F / 10.0F))
//                    .padding(8.dp)
//            )
            EcgPlot(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6F)
                    .padding(8.dp)
            )
        }
    }
}