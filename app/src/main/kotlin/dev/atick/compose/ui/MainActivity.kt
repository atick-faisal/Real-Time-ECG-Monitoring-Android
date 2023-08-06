package dev.atick.compose.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.R
import dev.atick.core.utils.extensions.checkForPermissions
import dev.atick.movesense.utils.BleUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleUtils: BleUtils

    private val permissions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JetpackComposeStarter)
        setContentView(R.layout.activity_main)

        // Configure required permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissions.add(Manifest.permission.RECORD_AUDIO)

        checkForPermissions(permissions) {
            Logger.i("ALL PERMISSIONS GRANTED")
        }

        bleUtils.initialize(this) {
            Logger.i("BLUETOOTH IS READY")
        }
    }

    override fun onResume() {
        bleUtils.setupBluetooth(this)
        super.onResume()
    }
}