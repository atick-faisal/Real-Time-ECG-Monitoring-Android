package dev.atick.compose.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.R
import dev.atick.compose.ui.common.components.InputField
import dev.atick.compose.ui.login.data.LoginState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        var username by viewModel.username.state
        var password by viewModel.password.state
        val loginState by viewModel.loginState

        val buttonText = when (loginState) {
            LoginState.LOGGED_OUT -> LoginState.LOGGED_OUT.text
            LoginState.LOGGING_IN -> LoginState.LOGGING_IN.text
            LoginState.LOGIN_SUCCESSFUL -> LoginState.LOGIN_SUCCESSFUL.text
        }

        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.width(100.dp),
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login to your Account",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                modifier = Modifier.fillMaxWidth(0.8F),
                value = username,
                onValueChange = { username = it },
                labelResourceId = R.string.username,
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(8.dp))

            InputField(
                modifier = Modifier.fillMaxWidth(0.8F),
                value = password,
                onValueChange = { password = it },
                labelResourceId = R.string.password,
                leadingIcon = Icons.Default.Password,
                isPasswordField = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { viewModel.login() }
            ) {
                Text(text = buttonText)
            }
        }
    }
}