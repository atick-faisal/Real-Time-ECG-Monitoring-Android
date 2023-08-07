package dev.atick.compose.ui.dashboard.components

import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import dev.atick.compose.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AudioRecordDialog(
    context: Context = LocalContext.current,
    onCompleteRecording: (File) -> Unit = { }
) {

    DisposableEffect(Unit) {

        val recorder = if (Build.VERSION.SDK_INT > 30) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        val timestamp = SimpleDateFormat("dd_M_yyyy_hh_mm_ss", Locale.US).format(Date())
        val savePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val myExternalFile = File(savePath, "cardiac_zone_${timestamp}.mp3")

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(myExternalFile)
        }

        recorder.prepare()
        recorder.start()

        onDispose {
            recorder.stop()
            recorder.release()
            onCompleteRecording(myExternalFile)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Recording ... ", fontSize = 20.sp)
        Text(text = "Please describe your situation", fontSize = 16.sp)
        AndroidView(
            factory = { ctx ->
                LottieAnimationView(ctx).apply {
                    setAnimation(R.raw.recording)
                    repeatCount = ValueAnimator.INFINITE
                    playAnimation()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .aspectRatio(1.0F)
        )
    }
}