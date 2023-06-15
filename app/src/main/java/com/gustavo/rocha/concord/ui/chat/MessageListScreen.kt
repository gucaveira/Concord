package com.gustavo.rocha.concord.ui.chat

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustavo.rocha.concord.R
import com.gustavo.rocha.concord.data.Author
import com.gustavo.rocha.concord.data.messageListSample
import com.gustavo.rocha.concord.ui.components.AsyncImage
import com.gustavo.rocha.concord.ui.components.AsyncImageProfile
import com.gustavo.rocha.concord.ui.components.MessageItemOther
import com.gustavo.rocha.concord.ui.components.MessageItemUser

@Composable
fun MessageScreen(
    state: MessageListUiState,
    modifier: Modifier = Modifier,
    onSendMessage: () -> Unit = {},
    onShowSelectorFile: () -> Unit = {},
    onShowSelectorStickers: () -> Unit = {},
    onDeselectMedia: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(topBar = {
        AppBarChatScreen(
            state = state,
            onBackClick = onBack
        )
    }) { paddingValues ->
        Column(
            modifier
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .padding(paddingValues)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(8f),
                reverseLayout = true
            ) {

                items(state.messages.reversed(), contentType = { it.author }) {
                    when (it.author) {
                        Author.OTHER -> {
                            MessageItemOther(it)
                        }

                        Author.USER -> {
                            MessageItemUser(it)
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(4.dp))

            if (state.mediaInSelection.isNotEmpty()) {
                SelectedMediaContainer(state, onDeselectMedia)
            }

            EntryTextBar(
                state = state,
                onShowSelectorFile = onShowSelectorFile,
                onClickSendMessage = onSendMessage,
                onAcessSticker = onShowSelectorStickers
            )
            Spacer(Modifier.height(2.dp))

        }

    }
}

@Composable
fun SelectedMediaContainer(
    state: MessageListUiState,
    onDeselectMedia: () -> Unit,
) {
    Divider(
        Modifier
            .height(0.4.dp)
            .alpha(0.5f),
        color = MaterialTheme.colorScheme.outline
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(5)),
            imageUrl = state.mediaInSelection
        )

        IconButton(onClick = { onDeselectMedia() },
            Modifier
                .padding(12.dp)
                .align(Alignment.TopEnd)
                .alpha(0.5f)
                .clickable { }
                .shadow(1.dp, RoundedCornerShape(100))
                .background(Color.Black, CircleShape)
                .size(22.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.icon_message_or_mic),
                modifier = Modifier.padding(4.dp),
                tint = Color.White
            )
        }
    }

}

@Composable
fun EntryTextBar(
    state: MessageListUiState,
    onShowSelectorFile: () -> Unit,
    onClickSendMessage: () -> Unit,
    onAcessSticker: () -> Unit,
) {
    val barHeight = 56.dp
    val hasContentToSend = state.hasContentToSend

    Row(
        Modifier
            .padding(4.dp)
            .height(barHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .shadow(1.dp, RoundedCornerShape(100))
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(100)
                )
                .weight(5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAcessSticker) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_sticker),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "file",
                    modifier = Modifier.weight(1f)
                )
            }

            BasicTextField(
                value = state.messageValue,
                onValueChange = state.onMessageValueChange,
                modifier = Modifier.weight(5F),
                textStyle = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerValue ->
                    Box {
                        if (state.messageValue.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.message), fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        innerValue()
                    }
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.surfaceVariant)
            )

            IconButton(
                onClick = onShowSelectorFile,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_action_files),
                    contentDescription = "file",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.rotate(-45f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onShowSelectorFile,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_action_cam),
                    contentDescription = "file",
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(onClick = {
            if (hasContentToSend) {
                onClickSendMessage()
            }
        },
            modifier = Modifier
                .clickable { }
                .shadow(1.dp, RoundedCornerShape(100))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(100)
                )
        ) {
            val micIcon = painterResource(id = R.drawable.ic_action_mic)
            val sendIcon = painterResource(id = R.drawable.ic_action_send)

            val transition = updateTransition(
                targetState = hasContentToSend,
                label = "Crossfade transition"
            )
            val iconSize by transition.animateDp(label = "Icon size") { if (it) 24.dp else 24.dp }
            val iconAlpha by transition.animateFloat(label = "Icon alpha") { if (it) 1f else 1f }

            Crossfade(targetState = hasContentToSend) { hasContent ->
                Icon(
                    if (hasContent) sendIcon else micIcon,
                    stringResource(R.string.icon_message_or_mic),
                    tint = Color.White,
                    modifier = Modifier
                        .size(iconSize)
                        .alpha(iconAlpha)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarChatScreen(state: MessageListUiState, onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onBackClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    tint = Color.White,
                    contentDescription = null
                )

                AsyncImageProfile(
                    imageUrl = state.profilePicOwner,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }
        },
        title = {
            Text(
                text = state.ownerName,
                fontWeight = FontWeight.Medium
            )

        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

@Preview
@Composable
fun ChatScreenPreview() {
    MessageScreen(
        MessageListUiState(
            messages = messageListSample,
        )
    )
}
