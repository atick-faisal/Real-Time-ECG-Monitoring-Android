package dev.atick.compose.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.network.data.LoginRequest
import dev.atick.network.repository.CardiacZoneRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val cardiacZoneRepository: CardiacZoneRepository
): BaseViewModel() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")

    fun login() {
        viewModelScope.launch {
            val response = cardiacZoneRepository.login(
                LoginRequest(
                    username = username.value,
                    password = password.value
                )
            )

            Logger.w("LOGIN: $response")
        }
    }
}