package dev.atick.compose.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Property<T>(
    val state: MutableState<T>,
    val error: MutableState<String?> = mutableStateOf(null)
)
