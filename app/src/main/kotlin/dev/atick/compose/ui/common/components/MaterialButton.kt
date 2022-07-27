package dev.atick.compose.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MaterialButton(
    modifier: Modifier = Modifier,
    textResourceId: Int,
    filled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)
) {
    return Button(
        modifier = modifier.then(Modifier.height(40.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                it.invoke()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = stringResource(id = textResourceId))
        }
    }
}