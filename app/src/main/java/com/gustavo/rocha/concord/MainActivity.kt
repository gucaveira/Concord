package com.gustavo.rocha.concord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.gustavo.rocha.concord.extensions.showLog
import com.gustavo.rocha.concord.navigation.ConcordNavHost
import com.gustavo.rocha.concord.ui.theme.ConcordTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filesDir.listFiles()?.forEach { file ->
            showLog("Arquivo: ${file.name}")
        }

        getExternalFilesDir(null)?.listFiles()?.forEach { file ->
            showLog("Arquivo externo: ${file.name}")
        }


        setContent {
            ConcordTheme {
                val navController = rememberNavController()
                ConcordNavHost(navController = navController)
            }
        }
    }
}