package dev.atick.compose.ui.dashboard.components

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.utils.Utils
import dev.atick.compose.R
import dev.atick.compose.ui.dashboard.data.EcgPlotData

@Composable
fun EcgPlot(
    data: EcgPlotData,
    modifier: Modifier = Modifier
) {
    val isDarkThemeEnabled = isSystemInDarkTheme()
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            Utils.init(ctx)
            CombinedChart(ctx).apply {
                description.text = ""
                axisLeft.setDrawLabels(false)
                axisLeft.isEnabled = false
                axisLeft.setDrawGridLines(false)
                axisLeft.axisLineWidth = 2.0F
                axisLeft.setDrawLabels(false)
                axisLeft.labelCount = 5
                axisRight.setDrawLabels(false)
                axisRight.isEnabled = false
                axisRight.axisLineWidth = 2.0F
                axisRight.setDrawGridLines(false)
                xAxis.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                xAxis.setDrawLabels(false)
                xAxis.setDrawGridLines(false)
                xAxis.labelCount = 8
                xAxis.axisLineWidth = 2.0F
                legend.isEnabled = false
                setTouchEnabled(false)
            }
        },
        update = { lineChart ->
            lineChart.axisLeft.labelCount = 5
            lineChart.xAxis.labelCount = 8
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
            data.ecg.apply {
                color = if (isDarkThemeEnabled)
                    context.getColor(R.color.backgroundLight)
                else context.getColor(R.color.backgroundDark)
                setDrawValues(false)
                setDrawFilled(false)
                setDrawCircleHole(false)
                setDrawCircles(false)
                lineWidth = 2.0F
            }

            data.rPeaks.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = context.getColor(R.color.r_peak)
                scatterShapeSize = 20.0F
            }

            data.vBeats.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = context.getColor(R.color.v_beat)
                scatterShapeSize = 20.0F
            }

            data.sBeats.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = context.getColor(R.color.s_beat)
                scatterShapeSize = 20.0F
            }

            val ecgData = LineData(data.ecg)
            val rPeakData = ScatterData(
                listOf(
                    data.rPeaks,
                    data.vBeats,
                    data.sBeats
                )
            )
            val combinedData = CombinedData()
            combinedData.setData(ecgData)
            combinedData.setData(rPeakData)
            combinedData.notifyDataChanged()
            lineChart.data = combinedData
            lineChart.invalidate()
        },
        modifier = modifier
    )
}