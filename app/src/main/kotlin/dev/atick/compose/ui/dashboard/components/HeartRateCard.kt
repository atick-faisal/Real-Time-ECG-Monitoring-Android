package dev.atick.compose.ui.dashboard.components

import android.animation.ValueAnimator
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import dev.atick.compose.R

@Composable
fun HeartRateCard(
    modifier: Modifier = Modifier,
    heartRate: Float = 0.0F
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(100.dp),
                painter = painterResource(id = R.drawable.plant),
                contentDescription = "Plant"
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Heart Rate",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = "%.1f".format(heartRate),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Thin,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = "BPM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onBackground
                )

                AndroidView(
                    factory = { ctx ->
                        LottieAnimationView(ctx).apply {
                            setAnimation(R.raw.heart_beat)
                            repeatCount = ValueAnimator.INFINITE
                            speed = 2.0F
                            playAnimation()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.4F)
                        .aspectRatio(1.6F)
                )
            }
        }
    }
}