
package com.example.mindfulgrowth.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun MessagesScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Messages Screen Content",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    MessagesScreen()
}
