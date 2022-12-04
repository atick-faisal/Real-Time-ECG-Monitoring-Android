package dev.atick.compose.ui.connection

import ai.atick.material.MaterialColor
import android.animation.ValueAnimator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieAnimationView
import dev.atick.compose.R
import dev.atick.compose.ui.connection.data.ConnectionUiState

@Composable
fun ConnectionScreen(
    onConnectClick: (String) -> Unit,
    viewModel: ConnectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    return Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connect",
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier.width(200.dp),
            painter = painterResource(id = R.drawable.movesense_logo),
            contentDescription = "Movesense Logo"
        )

        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState == ConnectionUiState.DEVICE_FOUND) {
                Box(
                    Modifier
                        .size(200.dp)
                        .background(MaterialColor.Green50, shape = CircleShape)
                )
            }

            Image(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = R.drawable.ic_movesense),
                contentDescription = "Movesense Icon",
                contentScale = ContentScale.FillBounds
            )

            if ((uiState == ConnectionUiState.SCANNING || uiState == ConnectionUiState.CONNECTING)) {
                AndroidView(
                    factory = { ctx ->
                        LottieAnimationView(ctx).apply {
                            setAnimation(R.raw.circular_lines)
                            repeatCount = ValueAnimator.INFINITE
                            playAnimation()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(text = uiState.description, color = MaterialTheme.colors.onBackground)

        Spacer(modifier = Modifier.height(64.dp))

        AnimatedVisibility(
            visible = uiState != ConnectionUiState.SCANNING
        ) {
            Button(
                onClick = {
                    when (uiState) {
                        ConnectionUiState.DEVICE_FOUND,
                        ConnectionUiState.CONNECTION_FAILED ->
                            onConnectClick(viewModel.movesenseMacAddress)
                        ConnectionUiState.SCAN_FAILED ->
                            viewModel.scan()
                        else -> Unit
                    }
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                when (uiState) {
                    ConnectionUiState.CONNECTING ->
                        CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
                    ConnectionUiState.SCAN_FAILED ->
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan Again"
                        )
                    else ->
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Connect"
                        )
                }
            }
        }

    }
}