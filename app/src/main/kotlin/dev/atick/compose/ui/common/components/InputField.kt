package dev.atick.compose.ui.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.atick.compose.R

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelResourceId: Int,
    isPasswordField: Boolean = false,
    leadingIcon: ImageVector,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    val visibilityIcon =
        if (passwordVisibility) Icons.Filled.Visibility
        else Icons.Filled.VisibilityOff

    return OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = labelResourceId)) },
        isError = errorMessage != null,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPasswordField && !passwordVisibility) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                imageVector = leadingIcon,
                contentDescription = stringResource(id = labelResourceId)
            )
        },
        trailingIcon = {
            if (isPasswordField) {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                    Icon(
                        imageVector = visibilityIcon,
                        contentDescription = stringResource(R.string.password_toggle)
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            leadingIconColor = MaterialTheme.colors.primaryVariant,
            trailingIconColor = MaterialTheme.colors.primaryVariant,
            textColor = MaterialTheme.colors.onSurface
        )

    )
}