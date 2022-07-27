package dev.atick.compose.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.R
import dev.atick.compose.ui.common.components.InputField
import dev.atick.compose.ui.common.components.MaterialButton
import dev.atick.compose.ui.connection.components.BtDeviceCard

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)) {
        var username by viewModel.username.state
        var password by viewModel.password.state

        Column(
            Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                textFieldValue = username,
                hintResourceId = R.string.username,
                labelResourceId = R.string.username,
                onValueChange = { username = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = Icons.Default.Person,
                        contentDescription = "",
//                        tint = MaterialTheme.colors.primary
                    )
                },
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "hello")
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "login")
                    }
                }
            }

//            InputField(
//                textFieldValue = password,
//                hintResourceId = R.string.password,
//                labelResourceId = R.string.password,
//                onValueChange = { password = it },
//                isPasswordField = true,
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Password,
//                        contentDescription = "Password"
//                    )
//                }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            MaterialButton(
//                modifier = Modifier.fillMaxWidth(),
                textResourceId = R.string.login,
                icon = {
                    Icon(
                        modifier = Modifier.width(20.dp),
                        imageVector = Icons.Default.Favorite,
                        contentDescription = ""
                    )
                }
            ) {
                
            }
        }
    }
}