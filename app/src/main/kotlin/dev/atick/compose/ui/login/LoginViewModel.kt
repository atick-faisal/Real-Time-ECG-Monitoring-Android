package dev.atick.compose.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Property
import dev.atick.network.data.LoginRequest
import dev.atick.network.repository.CardiacZoneRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val cardiacZoneRepository: CardiacZoneRepository
) : BaseViewModel() {
    val username = Property(mutableStateOf(""))
    val password = Property(mutableStateOf(""))

    fun login() {
        viewModelScope.launch {
            val request = LoginRequest(
                username = username.state.value,
                password = password.state.value
            )
            val response = cardiacZoneRepository.login(request)
            Logger.w("LOGIN: $response")
        }
    }
}