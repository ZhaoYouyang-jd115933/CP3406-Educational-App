package com.youyangzhao.sourcesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.youyangzhao.sourcesense.navigation.SourceSenseNavHost
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SourceSenseTheme {
                SourceSenseNavHost()
            }
        }
    }
}