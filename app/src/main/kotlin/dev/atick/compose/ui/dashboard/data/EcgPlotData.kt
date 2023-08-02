package dev.atick.compose.ui.dashboard.data

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import dev.atick.network.data.Ecg
import java.text.SimpleDateFormat
import java.util.*

data class EcgPlotData(
    val id: Long = 0L,
    val ecg: LineDataSet = LineDataSet(listOf(), "ECG"),
    val rPeaks: ScatterDataSet = ScatterDataSet(listOf(), "R-PEAK"),
    val vBeats: ScatterDataSet = ScatterDataSet(listOf(), "V-BEAT"),
    val sBeats: ScatterDataSet = ScatterDataSet(listOf(), "S-BEAT"),
    val af: Int = 0
) {
    fun getTimestamp(): String {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
            val netDate = Date(id)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}

fun List<Ecg>.toEcgPlotData(): List<EcgPlotData> {
    return map { ecg ->
        EcgPlotData(
            id = ecg.id,
            ecg = LineDataSet(
                ecg.ecgData.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.toFloat())
                },
                "ECG"
            ),
            rPeaks = ScatterDataSet(
                ecg.rPeaks.map { rPeak ->
                    Entry(
                        rPeak.x.toFloat(),
                        rPeak.y.toFloat()
                    )
                },
                "R-PEAK"
            ),
            vBeats = ScatterDataSet(
                ecg.vBeats.map { rPeak ->
                    Entry(
                        rPeak.x.toFloat(),
                        rPeak.y.toFloat()
                    )
                },
                "V-BEAT"
            ),
            sBeats = ScatterDataSet(
                ecg.sBeats.map { rPeak ->
                    Entry(
                        rPeak.x.toFloat(),
                        rPeak.y.toFloat()
                    )
                },
                "S-BEAT"
            ),
            af = ecg.af
        )
    }
}