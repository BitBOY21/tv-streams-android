package com.example.tvstreams

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tvstreams.ui.TvStreamsApp
import com.example.tvstreams.ui.theme.TvStreamsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TvStreamsTheme {
                TvStreamsApp()
            }
        }
    }
}
