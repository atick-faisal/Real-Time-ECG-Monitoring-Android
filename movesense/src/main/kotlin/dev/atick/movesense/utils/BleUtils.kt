package dev.atick.movesense.utils

import androidx.activity.ComponentActivity

interface BleUtils {

    fun initialize(activity: ComponentActivity, onSuccess: () -> Unit)
    fun isAllPermissionsProvided(activity: ComponentActivity): Boolean
    fun setupBluetooth(activity: ComponentActivity)

}