package com.example.tvstreams.model

/**
 * Simple in-memory repository that holds the list of available TV channels.
 * For now the data is hard-coded, but later this can be replaced with
 * network / database sources without changing the UI code.
 */
object ChannelRepository {

    /**
     * Static list of channels shown on the main screen.
     * - `id` must be unique and should not contain spaces (used for navigation).
     */
    val channels: List<Channel> = listOf(
        Channel(
            id = "kan11",
            name = "KAN 11",
            url = "https://kan11.media.kan.org.il/hls/live/2024514/2024514/master.m3u8"
        ),
        Channel(
            id = "knesset99",   // No spaces: used as a NavHost route parameter
            name = "Knesset 99",
            url = "https://kneset.gostreaming.tv/p2-kneset/_definst_/myStream/playlist.m3u8"
        ),
        Channel(
            id = "example3",
            name = "Example Channel 3",
            url = "https://kan11.media.kan.org.il/hls/live/2024514/2024514/master.m3u8"
        )
    )
}
