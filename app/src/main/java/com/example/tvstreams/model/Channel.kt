package com.example.tvstreams.model

/**
 * Represents a single TV channel in the app.
 *
 * @param id   Short, stable identifier used for navigation / routing.
 * @param name Display name shown in the UI.
 * @param url  Streaming URL (e.g. HLS .m3u8).
 */
data class Channel(
    val id: String,   // Short stable ID used for navigation
    val name: String,
    val url: String
)
