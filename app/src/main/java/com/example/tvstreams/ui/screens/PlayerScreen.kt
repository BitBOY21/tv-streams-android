package com.example.tvstreams.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

/**
 * Screen that plays a given TV channel using ExoPlayer and a custom control bar.
 *
 * @param channelName Name shown in the top app bar.
 * @param url         HLS / streaming URL of the channel.
 * @param onBack      Callback when the user presses the back arrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    channelName: String,
    url: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // ExoPlayer instance – created once for this composable and remembered.
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // UI state we want to survive configuration changes (rotation, etc.).
    var isPlaying by rememberSaveable { mutableStateOf(true) }
    var isVolumeSliderVisible by rememberSaveable { mutableStateOf(false) }
    var isFullscreen by rememberSaveable { mutableStateOf(false) }

    // Current volume (0f..1f). Normal remember is enough here.
    var volume by remember { mutableStateOf(1f) }

    // When url or exoPlayer changes, (re)load the media.
    LaunchedEffect(url, exoPlayer) {
        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
            this.volume = volume
        }
        isPlaying = true
    }

    // Clean up player + restore orientation when leaving this screen.
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Scaffold(
        topBar = {
            // In fullscreen mode we hide the top app bar.
            if (!isFullscreen) {
                TopAppBar(
                    title = { Text(channelName) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(if (isFullscreen) Color.Black else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // The actual video surface hosted inside a classic Android View.
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false   // Hide ExoPlayer's default controls
                        ViewCompat.setLayoutDirection(this, View.LAYOUT_DIRECTION_LTR)
                    }
                }
            )

            // Custom bottom control bar (play, live, volume, fullscreen).
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xAA000000))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .navigationBarsPadding(),   // Avoid overlap with system nav bar
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Left side – Play/Pause + LIVE button.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Play / Pause toggle.
                    IconButton(onClick = {
                        isPlaying = !isPlaying
                        exoPlayer.playWhenReady = isPlaying
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White
                        )
                    }

                    // LIVE button – jumps to the end of the stream (live edge).
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .background(Color.Red, RoundedCornerShape(4.dp))
                            .clickable {
                                val duration = exoPlayer.duration
                                if (duration != C.TIME_UNSET && duration > 0) {
                                    exoPlayer.seekTo(duration)
                                } else {
                                    exoPlayer.seekToDefaultPosition()
                                }
                                exoPlayer.playWhenReady = true
                                isPlaying = true
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Right side – Volume icon + Fullscreen icon.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Click toggles visibility of the floating volume slider.
                    IconButton(onClick = {
                        isVolumeSliderVisible = !isVolumeSliderVisible
                    }) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Volume",
                            tint = Color.White
                        )
                    }

                    // Fullscreen toggle – locks orientation to landscape when enabled.
                    IconButton(onClick = {
                        val act = activity
                        if (act != null) {
                            isFullscreen = !isFullscreen
                            act.requestedOrientation =
                                if (isFullscreen)
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                else
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }

            // Floating volume slider panel shown above the bottom bar.
            if (isVolumeSliderVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 16.dp,
                            bottom = 56.dp   // Slightly above the control bar
                        )
                        .background(Color(0xDD333333), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Slider(
                        value = volume,
                        onValueChange = { newVolume ->
                            volume = newVolume
                            exoPlayer.volume = newVolume
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}
