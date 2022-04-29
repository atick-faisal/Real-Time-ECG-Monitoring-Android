package dev.atick.compose.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    title: String,
    color: Color = MaterialTheme.colors.primary
) {
    Row(
        modifier = modifier.then(Modifier.fillMaxHeight()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
                .width(8.dp)
                .height(32.dp)
                .background(color)
                .padding(end = 16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}