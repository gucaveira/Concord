package com.gustavo.rocha.concord.navigation

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
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
            val context = LocalContext.current

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

                context.getExternalFilesDir("stickers")?.listFiles()?.forEach { file ->
                    stickerList.add(file.path)
                }

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

            val pickMedia = rememberLauncherForActivityResult(
                PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    viewModelMessage.loadMediaInScreen(uri.toString())
                }
            }

            val pickFile = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {

                    val name = context.contentResolver
                        .query(uri, null, null, null, null).use { cursor ->
                            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            cursor?.moveToFirst()
                            nameIndex?.let { cursor.getString(it) }
                        }

                    uiState.onMessageValueChange(name.toString())
                    viewModelMessage.loadMediaInScreen(uri.toString())
                    viewModelMessage.sendMessage()
                }
            }

            if (uiState.showBottomSheetFile) {
                ModalBottomSheetFile(
                    onSelectPhoto = {
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
                        viewModelMessage.setShowBottomSheetFile(false)
                    },
                    onSelectFile = {
                        // */* para buscar todos os arquivos
                        // "image/pgn", "application/pdf"
                        pickFile.launch(arrayOf("*/*"))
                        viewModelMessage.setShowBottomSheetFile(false)
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    })
            }
        }
    }
}

internal fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null,
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}