package dev.atick.compose.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.compose.ui.login.data.LoginState
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.compose.ui.utils.Property
import dev.atick.network.data.LoginRequest
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.storage.preferences.UserPreferences
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val cardiacZoneRepository: CardiacZoneRepository,
    private val userPreferences: UserPreferences
) : BaseViewModel() {
    val username = Property(mutableStateOf(""))
    val password = Property(mutableStateOf(""))
    val loginState = mutableStateOf(LoginState.LOGGED_OUT)

    private val _userId = MutableLiveData(Event("-1"))
    val userId: LiveData<Event<String>>
        get() = _userId

    init {
        viewModelScope.launch {
            userPreferences.getUserId().collect { id ->
                Logger.w("USER ID LOGIN: $id")
                _userId.postValue(Event(id))
            }
        }
    }

    fun login() {
        loginState.value = LoginState.LOGGING_IN
        viewModelScope.launch {
            val request = LoginRequest(
                username = username.state.value,
                password = password.state.value
            )

            val response = cardiacZoneRepository.login(request)

            Logger.w("LOGIN RESPONSE: $response")

            loginState.value = response?.patient?.patientId?.let { id ->
                userPreferences.saveUserId(id)
                _userId.postValue(Event(id))
                LoginState.LOGIN_SUCCESSFUL

            } ?: LoginState.LOGGED_OUT
        }
    }
}