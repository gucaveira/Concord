package com.gustavo.rocha.concord.navigation

import android.util.Log
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
import com.gustavo.rocha.concord.extensions.showMessage
import com.gustavo.rocha.concord.media.getAllImages
import com.gustavo.rocha.concord.media.getNameByUri
import com.gustavo.rocha.concord.media.imagePermission
import com.gustavo.rocha.concord.media.persistUriPermission
import com.gustavo.rocha.concord.media.verifyPermission
import com.gustavo.rocha.concord.ui.chat.MessageListViewModel
import com.gustavo.rocha.concord.ui.chat.MessageScreen
import com.gustavo.rocha.concord.ui.components.ModalBottomSheetFile
import com.gustavo.rocha.concord.ui.components.ModalBottomSheetSticker

internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"

fun NavGraphBuilder.messageListScreen(onBack: () -> Unit = {}) {

    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val uiState by viewModelMessage.uiState.collectAsState()
            val context = LocalContext.current

            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModelMessage.setShowBottomSheetSticker(true)
                } else {
                    context.showMessage(
                        "Permissão não concedida, não será possivel " +
                                "acessar os stickers sem ela",
                        true
                    )
                }
            }

            MessageScreen(
                state = uiState,
                onSendMessage = {
                    viewModelMessage.sendMessage()
                },
                onShowSelectorFile = {
                    viewModelMessage.setShowBottomSheetFile(true)
                },
                onShowSelectorStickers = {
                    if (context.verifyPermission(imagePermission())) {
                        requestPermissionLauncher.launch(imagePermission())
                    } else {
                        viewModelMessage.setShowBottomSheetSticker(true)
                    }
                },
                onDeselectMedia = {
                    viewModelMessage.deselectMedia()
                }, onBack = {
                    onBack()
                }
            )

            if (uiState.showBottomSheetSticker) {
                val stickerList = mutableStateListOf<String>()
                context.getAllImages { images ->
                    stickerList.addAll(images)
                }

                /*   context.getExternalFilesDir("stickers")?.listFiles()?.forEach { file ->
                       stickerList.add(file.path)
                   }*/

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
                    context.persistUriPermission(uri)
                    viewModelMessage.loadMediaInScreen(uri.toString())
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

            val pickFile = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    context.persistUriPermission(uri)

                    val name = context.getNameByUri(uri)

                    uiState.onMessageValueChange(name.toString())
                    viewModelMessage.loadMediaInScreen(uri.toString())
                    viewModelMessage.sendMessage()
                } else {
                    Log.d("FilePicker", "No media selected")
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