package com.gustavo.rocha.concord.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustavo.rocha.concord.R
import com.gustavo.rocha.concord.data.Chat
import com.gustavo.rocha.concord.data.chatListSample
import com.gustavo.rocha.concord.ui.components.AsyncImage
import com.gustavo.rocha.concord.ui.theme.ConcordTheme

@Composable
fun ChatListScreen(
    state: ChatListUiState,
    modifier: Modifier = Modifier,
    onOpenChat: (Long) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
) {
    Scaffold(
        topBar = { AppBarChatList() },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { onSendNewMessage() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_message),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(id = R.string.send_new_messa)
                )
            }
        }) { paddingValues ->
        LazyColumn(modifier.padding(paddingValues)) {
            items(state.chats) { chat ->
                ChatItem(chat) { chatId ->
                    onOpenChat(chatId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarChatList() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Medium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun ChatItem(chat: Chat, onClick: (Long) -> Unit) {
    Column(Modifier.clickable { onClick(chat.id) }) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(Modifier.padding(horizontal = 16.dp)) {
            AsyncImage(
                imageUrl = chat.profilePicOwner,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(
                Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(
                        text = chat.owner,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = chat.dateLastMessage,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(0.2f)
                    )
                }

                Text(
                    text = chat.lastMessage.ifEmpty { LocalContext.current.getString(R.string.media) },
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun ChatListPreview() {
    ConcordTheme {
        ChatListScreen(
            state = ChatListUiState(null, chatListSample)
        )
    }
}

@Preview
@Composable
fun ChatListItemPreview() {
    ConcordTheme {
        ChatItem(chatListSample.first()) {}
    }
}