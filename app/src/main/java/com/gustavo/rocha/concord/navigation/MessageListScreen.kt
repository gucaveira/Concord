package com.gustavo.rocha.concord.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gustavo.rocha.concord.ui.chat.MessageListViewModel
import com.gustavo.rocha.concord.ui.chat.MessageScreen
import com.gustavo.rocha.concord.ui.components.ModalBottomSheetFile
import com.gustavo.rocha.concord.ui.components.ModalBottomSheetSticker

internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"

fun NavGraphBuilder.messageListScreen(onBack: () -> Unit = {}) {

    composable(messageChatFullPath) { navBackStackEntry ->
        navBackStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val uiState by viewModelMessage.uiState.collectAsState()

            MessageScreen(
                state = uiState,
                onSendMessage = {
                    viewModelMessage.sendMessage()
                },
                onShowSelectorFile = {
                    viewModelMessage.setShowBottomSheetFile(true)
                },
                onShowSelectorStickers = {
                    viewModelMessage.setShowBottomSheetSticker(true)
                },
                onDeselectMedia = {
                    viewModelMessage.deselectMedia()
                }, onBack = {
                    onBack()
                }
            )

            if (uiState.showBottomSheetSticker) {
                val stickerList = mutableStateListOf<String>()
                ModalBottomSheetSticker(
                    stickerList = stickerList,
                    onSelectedSticker = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                        viewModelMessage.loadMediaInScreen(path = it.toString())
                        viewModelMessage.sendMessage()
                    },
                    onBack = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                    })
            }

            if (uiState.showBottomSheetFile) {
                ModalBottomSheetFile(
                    onSelectPhoto = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    },
                    onSelectFile = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    })
            }
        }
    }
}

internal fun NavController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null,
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}