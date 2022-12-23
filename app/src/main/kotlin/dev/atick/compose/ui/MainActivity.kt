package dev.atick.compose.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.R
import dev.atick.core.utils.extensions.setupAppNotification
import dev.atick.movesense.utils.BleUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleUtils: BleUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JetpackComposeStarter)
        setContentView(R.layout.activity_main)

        setupAppNotification()
        bleUtils.initialize(this) {
            Logger.i("BLUETOOTH IS READY")
        }
    }

    override fun onResume() {
        bleUtils.setupBluetooth(this)
        super.onResume()
    }
}