package dev.atick.compose.ui.dashboard

import android.animation.ValueAnimator
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieAnimationView
import dev.atick.compose.R
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.home.components.EcgPlot

@Composable
fun DashboardScreen(
    onExitClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    return Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBar(
                title = "Dashboard",
                onExitClick = onExitClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(64.dp),
                onClick = { /*TODO*/ }
            ) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = "Add Doctor")
            }
        }
    ) {


        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFFf5f5eb)
                    ) {
                        Box {
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
                                    text = "%.1f".format(uiState.heartRate),
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

                            Image(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .width(100.dp),
                                painter = painterResource(id = R.drawable.plant),
                                contentDescription = "Plant"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                // modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                                text = "Live ECG Signal",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(Modifier.fillMaxWidth()) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.6F),
                                    painter = painterResource(id = R.drawable.ecg_background),
                                    contentDescription = "ECG Background",
                                    contentScale = ContentScale.FillBounds
                                )
                                EcgPlot(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.6F)
                                        .padding(16.dp),
                                    data = uiState.ecgPlotData
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
//                            .padding(horizontal = 16.dp)
//                            .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(50),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(text = "Record Data")
                            }
                        }


                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFFf9f0f3)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Abnormal Beats",
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
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(uiState.abnormalEcgPlotData) { ecgPlotData ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            // modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                            text = ecgPlotData.getTimestamp(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colors.onBackground
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(Modifier.fillMaxWidth()) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.6F),
                                painter = painterResource(id = R.drawable.ecg_background),
                                contentDescription = "ECG Background",
                                contentScale = ContentScale.FillBounds
                            )
                            EcgPlot(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.6F)
                                    .padding(16.dp),
                                data = ecgPlotData
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}
