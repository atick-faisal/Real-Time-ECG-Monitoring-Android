package dev.atick.compose.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    textFieldValue: String,
    hintResourceId: Int,
    labelResourceId: Int? = null,
    isPasswordField: Boolean = false,
    onValueChange: (String) -> Unit,
    onClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null
) {
    var textFieldModifier = if (singleLine) {
        Modifier.fillMaxWidth()
    } else {
        Modifier
            .fillMaxWidth()
            .height(150.dp)
    }
    var passwordVisibility by remember { mutableStateOf(false) }
    val visibilityIcon =
        if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    if (onClick != null) {
        textFieldModifier = textFieldModifier.clickable {
            onClick.invoke()
        }
    }
    Column(
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        // -------------------- LABEL ----------------------- //
        labelResourceId?.let {
            Text(
                stringResource(id = labelResourceId),
                color = MaterialTheme.colors.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ------------------- TEXT FIELD -------------------- //
        OutlinedTextField(
            value = if (keyboardOptions.keyboardType == KeyboardType.Number) {
                textFieldValue.filter { it.isDigit() }
            } else {
                textFieldValue
            },
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(id = hintResourceId)) },
            enabled = onClick == null,
            readOnly = onClick != null,
            singleLine = singleLine,
            modifier = textFieldModifier,
            isError = errorMessage != null,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = if (errorMessage == null) {
                    MaterialTheme.colors.onSurface
                } else {
                    MaterialTheme.colors.error
                }
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPasswordField && !passwordVisibility) {
                PasswordVisualTransformation()
            } else {
                visualTransformation
            },
            shape = RoundedCornerShape(28.dp),
            leadingIcon = leadingIcon,
            trailingIcon = if (isPasswordField) {
                {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            imageVector = visibilityIcon,
                            contentDescription = ""
                        )
                    }
                }
            } else {
                trailingIcon
            },
        )

        // ---------------------- ERROR MESSAGE ------------------- //
        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 3.dp)
                )
            }
        }
    }
}