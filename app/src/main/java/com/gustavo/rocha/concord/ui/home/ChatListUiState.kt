package com.gustavo.rocha.concord.ui.home

import com.gustavo.rocha.concord.data.Chat

data class ChatListUiState(
    val selectedId: Long? = null,
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = true,
)