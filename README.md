# TV Streams Android

Simple Android app for watching live TV streams (HLS) using **Kotlin**, **Jetpack Compose** and **ExoPlayer**.

## Features

- Channel list screen with a modern Compose UI
- Custom video player controls:
  - Play / Pause
  - “LIVE” jump to live position
  - Volume popup slider
  - Fullscreen mode (forces landscape)
- Support for HLS (`.m3u8`) streams

## Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose, Material 3  
- **Navigation:** `navigation-compose`  
- **Video:** ExoPlayer + PlayerView  
- **Architecture:** Simple single-activity app with screens split into packages
  - `model/` – channel data and repository
  - `ui/screens/` – `ChannelListScreen`, `PlayerScreen`
  - `ui/TvStreamsApp.kt` – navigation graph

## Customize Channels

You can easily change the list of TV channels used by the app.

1. Open `model/ChannelRepository.kt`.
2. Edit the `channels` list and add/remove `Channel` items.  
   Example:

   ```kotlin
   val channels: List<Channel> = listOf(
       Channel(
           id = "____",
           name = "____",
           url = "_____"
       )
   )
   
Notes:
id must be unique and simple (no spaces) because it is used in the navigation route.
name is the label shown in the channel list.
url must be a valid HLS .m3u8 stream URL.
After editing, rebuild and run the app in Android Studio.


