package dev.atick.compose.ui.dashboard.components

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
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
    
    // Get color resources in composable context
    val backgroundLightColor = colorResource(R.color.backgroundLight).toArgb()
    val backgroundDarkColor = colorResource(R.color.backgroundDark).toArgb()
    val rPeakColor = colorResource(R.color.r_peak).toArgb()
    val vBeatColor = colorResource(R.color.v_beat).toArgb()
    val sBeatColor = colorResource(R.color.s_beat).toArgb()

    AndroidView(
        factory = { ctx ->
            Utils.init(ctx)
            CombinedChart(ctx).apply {
                description.text = ""
                axisLeft.isDrawLabelsEnabled = false
                axisLeft.isEnabled = false
                axisLeft.isDrawGridLinesEnabled = false
                axisLeft.axisLineWidth = 2.0F
                axisLeft.labelCount = 5
                axisRight.isDrawLabelsEnabled = false
                axisRight.isEnabled = false
                axisRight.axisLineWidth = 2.0F
                axisRight.isDrawGridLinesEnabled = false
                xAxis.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                xAxis.isDrawLabelsEnabled = false
                xAxis.isDrawGridLinesEnabled = false
                xAxis.labelCount = 8
                xAxis.axisLineWidth = 2.0F
                legend.isEnabled = false
                isTouchEnabled = false
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
                    backgroundLightColor
                else backgroundDarkColor
                isDrawValuesEnabled = false
                isDrawFilledEnabled = false
                isDrawCircleHoleEnabled = false
                isDrawCirclesEnabled = false
                lineWidth = 2.0F
            }

            // MPAndroidChart 4 reads scatterShapeSize in dp (3.x used px), so 10 dp keeps the
            // old 20 px look on xhdpi screens.
            // https://github.com/PhilJay/MPAndroidChart/blob/v4.0.1/MIGRATION.md
            data.rPeaks.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = rPeakColor
                scatterShapeSize = 10.0F
            }

            data.vBeats.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = vBeatColor
                scatterShapeSize = 10.0F
            }

            data.sBeats.apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                color = sBeatColor
                scatterShapeSize = 10.0F
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
            combinedData.lineData = ecgData
            combinedData.scatterData = rPeakData
            combinedData.notifyDataChanged()
            lineChart.data = combinedData
            lineChart.invalidate()
        },
        modifier = modifier
    )
}