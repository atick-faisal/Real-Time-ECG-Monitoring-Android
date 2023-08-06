package dev.atick.core.utils.extensions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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

//fun ComponentActivity.permissionLauncher(
//    onSuccess: () -> Unit = {},
//    onFailure: () -> Unit = {}
//): ActivityResultLauncher<Array<String>> {
//    val resultCallback = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        val granted = permissions.entries.all { it.value }
//        if (granted) onSuccess.invoke()
//        else onFailure.invoke()
//    }
//    return resultCallback
//}

inline fun ComponentActivity.permissionLauncher(
    crossinline onSuccess: () -> Unit = {},
    crossinline onFailure: () -> Unit = {},
): ActivityResultLauncher<Array<String>> {
    val resultCallback = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            onSuccess.invoke()
        } else {
            onFailure.invoke()
        }
    }
    return resultCallback
}

inline fun ComponentActivity.checkForPermissions(
    permissions: List<String>,
    crossinline onSuccess: () -> Unit,
) {
    if (isAllPermissionsGranted(permissions)) return
    val launcher = permissionLauncher(
        onSuccess = onSuccess,
        onFailure = {
            showToast("PLEASE ALLOW ALL PERMISSIONS")
            openPermissionSettings()
        },
    )
    launcher.launch(permissions.toTypedArray())
}

fun ComponentActivity.openPermissionSettings() {
    val intent = Intent(
        ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName"),
    )
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}