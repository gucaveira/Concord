package com.gustavo.rocha.concord.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustavo.rocha.concord.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetSticker(
    stickerList: MutableList<String> = mutableListOf(),
    onSelectedSticker: (Uri) -> Unit = {},
    onBack: () -> Unit = {},
) {

    val modalSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = modalSheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            BottomSheetStickers(
                stickerList = stickerList,
                onSelectedSticker = { uri ->
                    onSelectedSticker(uri)
                }
            )
        },
        onDismissRequest = { onBack() }
    )
}

@Composable
private fun BottomSheetStickers(
    stickerList: MutableList<String>,
    onSelectedSticker: (Uri) -> Unit = {},
) {

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stickers",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(stickerList) { item ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clickable {
                        onSelectedSticker(Uri.parse(item))
                    }
                ) {
                    coil.compose.AsyncImage(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Inside,
                        model = item,
                        placeholder = painterResource(R.drawable.image_place_holder),
                        error = painterResource(R.drawable.image_place_holder),
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun BottomSheetStickersPreview() {
    BottomSheetStickers(mutableListOf())
}
