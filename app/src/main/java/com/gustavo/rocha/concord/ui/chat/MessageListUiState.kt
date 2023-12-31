package com.gustavo.rocha.concord.ui.chat

import com.gustavo.rocha.concord.data.Message

data class MessageListUiState(
    val messages: List<Message> = emptyList(),
    val messageValue: String = "",
    val onMessageValueChange: (String) -> Unit = {},
    val onMediaInSelectionChange: (String) -> Unit = {},
    val hasContentToSend: Boolean = false,
    val onHasContentToSend: (Boolean) -> Unit = {},
    val error: String = "",
    val mediaInSelection: String = "",
    val profilePicOwner: String = "",
    val ownerName: String = "",
    val hasImagePermission: Boolean = false,
    val showBottomSheetSticker: Boolean = false,
    val showBottomSheetFile: Boolean = false,
)
