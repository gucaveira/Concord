package com.gustavo.rocha.concord.media

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.gustavo.rocha.concord.extensions.showLog

fun Context.getAllImages(onLoadImages: (List<String>) -> Unit) {
    val images = mutableListOf<String>()

    val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA)
    val selection = "${MediaStore.Images.Media.DATA} LIKE '%/Download/stickers/%' " +
            "AND ${MediaStore.Images.Media.SIZE} > ?"
    val selectionArgs = arrayOf("70000")
    val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} DESC"

    contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->

        showLog("Total de imagens: ${cursor.count}")

        while (cursor.moveToNext()) {
            val nameIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val name: String = cursor.getString(nameIndex)

            val pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = cursor.getString(pathIndex)


            showLog("Nome: $name e caminho: $path")
            images.add(path)
        }
        onLoadImages(images)
    }
}

fun Context.getNameByUri(uri: Uri): String? {
    return contentResolver.query(uri, null, null, null, null)
        .use { cursor ->
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()
            nameIndex?.let { cursor.getString(it) }
        }

}
