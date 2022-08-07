package dev.atick.compose.ui.login

enum class LoginState(text: String) {
    LOGGED_OUT("Login"),
    LOGGING_IN("Logging In ..."),
    LOGIN_SUCCESSFUL("Login Successful")
}