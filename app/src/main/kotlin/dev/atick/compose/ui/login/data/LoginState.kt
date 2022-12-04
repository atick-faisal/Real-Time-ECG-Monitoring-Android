package dev.atick.compose.ui.login.data

enum class LoginState(val text: String) {
    LOGGED_OUT("Login"),
    LOGGING_IN("Logging In ..."),
    LOGIN_SUCCESSFUL("Login Successful")
}