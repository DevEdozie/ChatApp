package com.edozie.chatapp.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageBubble(text: String, isMe: Boolean) {
//    val alignment = if (isMe) Alignment.End else Alignment.Start
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bg = if (isMe) Color(0xFFDCF8C6) else Color.White

    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        contentAlignment = alignment
    ) {
        Surface(shape = RoundedCornerShape(8.dp), color = bg) {
            Text(text, Modifier.padding(8.dp))
        }
    }
}