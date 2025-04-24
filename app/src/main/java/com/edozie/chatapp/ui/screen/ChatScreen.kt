package com.edozie.chatapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edozie.chatapp.R
import com.edozie.chatapp.ui.widget.MessageBubble
import com.edozie.chatapp.viewmodel.AuthViewModel
import com.edozie.chatapp.viewmodel.ChatViewModel


@Composable
fun ChatScreen(vm: ChatViewModel, authVm: AuthViewModel = hiltViewModel()) {

    val msgs by vm.messages.collectAsState()
    val isTyping by vm.typing.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .weight(0.9f)
                .padding(8.dp),
            reverseLayout = false
        ) {
            // CHAT UI
            items(msgs) { m ->
                val isMe = m.senderId == authVm.getCurrentUser()!!.uid
                MessageBubble(m.text, isMe)
            }
            if (isTyping) {
                item { Text("${vm.otherEmail} is typing...", Modifier.padding(8.dp)) }
            }
        }
        // Message Box
        Surface(
            color = Color.White,
            modifier = Modifier
//                .height(56.dp)
                .weight(0.1f)
                .fillMaxWidth(),

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    placeholder = {
                        Text("Write a message...")
                    },
                    value = input,
                    onValueChange = {
                        input = it
                        vm.onTyping(it.isNotBlank())
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF007AFF),
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp)),
                    onClick = {
                        // Send message
                        vm.send(input.trim())
                        input = ""
                        vm.onTyping(false)
                    }) {
                    Icon(
                        painter = painterResource(R.drawable.send_text_ic),
                        "Send text",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }

            }
        }
    }
}
