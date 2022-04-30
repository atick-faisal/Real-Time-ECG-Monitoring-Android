package dev.atick.compose.ui.home.components

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import dev.atick.compose.R

@Composable
fun LinePlot(
    dataset: LineDataSet,
    modifier: Modifier = Modifier
) {

    val isDarkThemeEnabled = isSystemInDarkTheme()
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            Utils.init(ctx)
            LineChart(ctx).apply {
                description.text = ""
                axisLeft.setDrawLabels(false)
                axisLeft.isEnabled = true
                axisLeft.setDrawGridLines(false)
                axisLeft.axisLineWidth = 2.0F
                axisLeft.setDrawLabels(true)
                axisLeft.labelCount = 3
                axisRight.setDrawLabels(false)
                axisRight.isEnabled = false
                xAxis.setDrawLabels(true)
                xAxis.isEnabled = true
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.axisLineWidth = 2.0F
                legend.isEnabled = false
                setTouchEnabled(false)
            }
        },
        update = { lineChart ->
            lineChart.xAxis.labelCount = 3
            lineChart.axisLeft.textColor = if (isDarkThemeEnabled) {
                Color.LTGRAY
            } else {
                Color.DKGRAY
            }
            lineChart.xAxis.textColor = if (isDarkThemeEnabled) {
                Color.LTGRAY
            } else {
                Color.DKGRAY
            }
            dataset.apply {
                color = context.getColor(R.color.primary)
                if (isDarkThemeEnabled) {
                    this.fillColor = context.getColor(R.color.primary)
                    this.fillAlpha = 40
                } else {
                    this.fillColor = context.getColor(R.color.primary)
                    this.fillAlpha = 40
                }
                setDrawValues(false)
                setDrawFilled(true)
                setDrawCircleHole(false)
                setDrawCircles(false)
                lineWidth = 2.0F
            }
            val lineData = LineData(dataset)
            lineData.notifyDataChanged()
            lineChart.data = lineData
            lineChart.invalidate()
        },
        modifier = modifier
    )
}