package com.edozie.chatapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val listState = rememberLazyListState()

    // Whenever msgs changes, scroll to bottom
    LaunchedEffect(msgs.size) {
        if (msgs.isNotEmpty()) {
            listState.animateScrollToItem(msgs.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(0.8f)
                .padding(8.dp),
            reverseLayout = false
        ) {
            // CHAT UI
            items(msgs) { m ->
                val isMe = m.senderId == authVm.getCurrentUser()!!.uid
                MessageBubble(m.text, isMe)
            }
            if (isTyping) {
                item {
                    val name = vm.otherEmail.substringBefore("@")
                    Text(
                        text = "$name is typing…",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }


        // Input bar
        Surface(
            color = Color.White,
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    shape = RoundedCornerShape(24.dp),
                    placeholder = {
                        Text(
                            "Write a message...",
                            color = Color.Gray
                        )
                    },
                    value = input,
                    onValueChange = {
                        input = it
                        vm.onTyping(it.isNotBlank())
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF007AFF),
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF007AFF)),
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
