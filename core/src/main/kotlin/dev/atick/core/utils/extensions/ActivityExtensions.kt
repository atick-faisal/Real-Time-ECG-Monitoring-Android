package dev.atick.core.utils.extensions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.orhanobut.logger.Logger

fun ComponentActivity.resultLauncher(
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}

): ActivityResultLauncher<Intent> {
    val resultCallback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSuccess.invoke()
        } else {
            onFailure.invoke()
        }
    }
    return resultCallback
}

fun ComponentActivity.permissionLauncher(
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
): ActivityResultLauncher<Array<String>> {
    val resultCallback = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) onSuccess.invoke()
        else onFailure.invoke()
    }
    return resultCallback
}

fun ComponentActivity.setupAppNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val resultLauncher = this.permissionLauncher(
            onSuccess = { Logger.i("Notification Permissions Granted") },
            onFailure = { this.finishAffinity() }
        )
        resultLauncher.launch(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        )
    }
}