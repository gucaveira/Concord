package com.gustavo.rocha.concord.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gustavo.rocha.concord.ui.home.ChatListScreen
import com.gustavo.rocha.concord.ui.home.ChatListViewModel

internal const val chatListRoute = "chat"

fun NavGraphBuilder.chatListScreen(
    onOpenChat: (Long) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
) {
    composable(chatListRoute) {
        val chatViewModel = hiltViewModel<ChatListViewModel>()
        val chatState by chatViewModel.uiState.collectAsState()

        if (chatState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            ChatListScreen(
                state = chatState,
                onOpenChat = {
                    onOpenChat(it)
                },
                onSendNewMessage = onSendNewMessage
            )
        }
    }
}