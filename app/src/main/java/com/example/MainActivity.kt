package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.player.StreamPlayerView
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProviderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSurface
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

enum class NavDestination {
    HOME,
    LIBRARY,
    SEARCH,
    SETTINGS,
    DETAIL,
    PROVIDER
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleDeepLink(intent)
        setContent {
            MyApplicationTheme {
                StreamVerseApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val code = data.getQueryParameter("code")
            if (!code.isNullOrBlank()) {
                Toast.makeText(this, "Simkl authorization code received!", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun StreamVerseApp(viewModel: MainViewModel) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var previousDestination by remember { mutableStateOf(NavDestination.HOME) }

    val playerState by viewModel.playerState.collectAsState()

    // Handle Hardware Back Button
    BackHandler(enabled = playerState.isOpen || currentDestination == NavDestination.DETAIL || currentDestination == NavDestination.PROVIDER) {
        if (playerState.isOpen) {
            viewModel.closePlayer()
        } else if (currentDestination == NavDestination.DETAIL || currentDestination == NavDestination.PROVIDER) {
            currentDestination = previousDestination
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(StreamBackground)) {
        Scaffold(
            bottomBar = {
                if (!playerState.isOpen && currentDestination != NavDestination.DETAIL) {
                    NavigationBar(
                        containerColor = StreamSurface,
                        contentColor = StreamTextPrimary,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        val navItems = listOf(
                            Triple(NavDestination.HOME, "Home", Icons.Default.Home),
                            Triple(NavDestination.LIBRARY, "Library", Icons.Default.VideoLibrary),
                            Triple(NavDestination.SEARCH, "Search", Icons.Default.Search),
                            Triple(NavDestination.SETTINGS, "Settings", Icons.Default.Settings)
                        )

                        navItems.forEach { (dest, label, icon) ->
                            val isSelected = currentDestination == dest
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentDestination != dest) {
                                        previousDestination = currentDestination
                                        currentDestination = dest
                                    }
                                },
                                icon = {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        tint = if (isSelected) StreamPrimary else StreamTextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) StreamPrimary else StreamTextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = StreamPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_${label.lowercase()}")
                            )
                        }
                    }
                }
            },
            containerColor = StreamBackground,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    NavDestination.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { id, type ->
                                viewModel.loadDetail(id, type)
                                previousDestination = NavDestination.HOME
                                currentDestination = NavDestination.DETAIL
                            },
                            onNavigateToProvider = {
                                previousDestination = NavDestination.HOME
                                currentDestination = NavDestination.PROVIDER
                            },
                            onNavigateToSearch = {
                                previousDestination = NavDestination.HOME
                                currentDestination = NavDestination.SEARCH
                            },
                            onNavigateToSettings = {
                                previousDestination = NavDestination.HOME
                                currentDestination = NavDestination.SETTINGS
                            }
                        )
                    }

                    NavDestination.PROVIDER -> {
                        ProviderScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = previousDestination },
                            onNavigateToDetail = { id, type ->
                                viewModel.loadDetail(id, type)
                                currentDestination = NavDestination.DETAIL
                            }
                        )
                    }

                    NavDestination.DETAIL -> {
                        DetailScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = previousDestination },
                            onNavigateToMedia = { id, type ->
                                viewModel.loadDetail(id, type)
                            }
                        )
                    }

                    NavDestination.SEARCH -> {
                        SearchScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = NavDestination.HOME },
                            onNavigateToDetail = { id, type ->
                                viewModel.loadDetail(id, type)
                                previousDestination = NavDestination.SEARCH
                                currentDestination = NavDestination.DETAIL
                            }
                        )
                    }

                    NavDestination.LIBRARY -> {
                        LibraryScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { id, type ->
                                viewModel.loadDetail(id, type)
                                previousDestination = NavDestination.LIBRARY
                                currentDestination = NavDestination.DETAIL
                            }
                        )
                    }

                    NavDestination.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = previousDestination }
                        )
                    }
                }
            }
        }

        // Full Screen Internal Video Player Layer
        AnimatedVisibility(
            visible = playerState.isOpen && playerState.activeSource != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            playerState.activeSource?.let { source ->
                StreamPlayerView(
                    title = playerState.title,
                    subtitle = playerState.subtitle,
                    source = source,
                    allSources = playerState.allSources,
                    initialPositionMs = playerState.initialPosMs,
                    hasNextEpisode = playerState.hasNextEpisode,
                    hasPrevEpisode = playerState.hasPrevEpisode,
                    onNextEpisode = { viewModel.playNextEpisode() },
                    onPrevEpisode = { viewModel.playPrevEpisode() },
                    onSourceSelected = { newSrc -> viewModel.switchPlayerSource(newSrc) },
                    onProgressUpdate = { cur, dur, fin ->
                        viewModel.updatePlaybackProgress(cur, dur, fin)
                    },
                    onClose = { viewModel.closePlayer() }
                )
            }
        }
    }
}
