package com.edozie.chatapp.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edozie.chatapp.data.room.ChatThreadEntity
import java.text.DateFormat
import java.util.Date


@Composable
fun ChatThreadItem(thread: ChatThreadEntity, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(thread.otherUserEmail, fontWeight = FontWeight.Bold)
            thread.lastMessage?.let { Text(it, maxLines = 1) }
        }
        Text(
            DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(Date(thread.timesStamp))
        )
    }
}