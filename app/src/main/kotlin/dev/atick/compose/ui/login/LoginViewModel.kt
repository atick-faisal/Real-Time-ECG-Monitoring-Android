package dev.atick.compose.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.core.utils.Property
import dev.atick.network.data.LoginRequest
import dev.atick.network.repository.CardiacZoneRepository
import dev.atick.storage.preferences.UserPreferences
import kotlinx.coroutines.flow.collect
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

    private val _userId = MutableLiveData(Event(0))
    val userId: LiveData<Event<Int>>
        get() = _userId

    init {
        viewModelScope.launch {
            userPreferences.getUserId().collect { id ->
                Logger.w("USER ID: $id")
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
            _userId.postValue(Event(response.patientId))

            if (response.success) {
                loginState.value = LoginState.LOGIN_SUCCESSFUL
                userPreferences.saveUserId(response.patientId)
            } else {
                loginState.value = LoginState.LOGGED_OUT
            }
        }
    }
}