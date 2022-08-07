package dev.atick.compose.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
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
    private val _loginState = MutableLiveData(Event(LoginState.LOGGED_OUT))
    val loginState: LiveData<Event<LoginState>>
        get() = _loginState

    fun login() {
        _loginState.postValue(Event(LoginState.LOGGING_IN))
        viewModelScope.launch {
            val request = LoginRequest(
                username = username.state.value,
                password = password.state.value
            )
            val response = cardiacZoneRepository.login(request)

            if (response != null) {
                _loginState.postValue(Event(LoginState.LOGIN_SUCCESSFUL))
            } else {
                _loginState.postValue(Event(LoginState.LOGGED_OUT))
            }
        }
    }
}