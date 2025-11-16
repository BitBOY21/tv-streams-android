package com.example.tvstreams.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tvstreams.model.ChannelRepository
import com.example.tvstreams.ui.screens.ChannelListScreen
import com.example.tvstreams.ui.screens.PlayerScreen

/**
 * Root composable of the app.
 *
 * Holds the NavHost and wires navigation between:
 * - Channel list screen
 * - Player screen
 */
@Composable
fun TvStreamsApp() {
    val navController = rememberNavController()

    // Channel data source – currently provided by an in-memory repository.
    val channels = remember { ChannelRepository.channels }

    NavHost(
        navController = navController,
        startDestination = "channel_list"
    ) {
        // Channel list screen – shows all available channels.
        composable("channel_list") {
            ChannelListScreen(
                channels = channels,
                onChannelClick = { channel ->
                    // Navigate to player, passing the channel id as a route argument.
                    navController.navigate("player/${channel.id}")
                }
            )
        }

        // Player screen – receives only the channel id as a navigation argument.
        composable(
            route = "player/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            val channel = channels.firstOrNull { it.id == id } ?: return@composable

            PlayerScreen(
                channelName = channel.name,
                url = channel.url,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
