package dev.atick.core.utils.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import dev.atick.core.BuildConfig

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.debugMessage(error: String) {
    if (BuildConfig.DEBUG) {
        this.showToast(error)
    }
}

fun Context.hasPermission(permissionType: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permissionType) ==
        PackageManager.PERMISSION_GRANTED
}

fun Context.showAlertDialog(
    title: String,
    message: String,
    onApprove: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton("OK") { _, _ ->
        onApprove.invoke()
    }

    builder.setNegativeButton("CANCEL") { _, _ ->
        onCancel.invoke()
    }

    builder.setCancelable(false)
    builder.show()
}