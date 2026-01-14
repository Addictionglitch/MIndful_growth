
package com.example.mindfulgrowth.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindfulgrowth.ui.screens.feed.FeedScreen
import com.example.mindfulgrowth.ui.screens.home.HomeScreen
import com.example.mindfulgrowth.ui.screens.profile.ProfileScreen
import com.example.mindfulgrowth.ui.screens.settings.SettingsScreen
import com.example.mindfulgrowth.ui.screens.stats.StatsScreen
import kotlinx.coroutines.launch

object Routes {
    const val MAIN_PAGER = "main_pager"
    const val SETTINGS = "settings"
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Feed : Screen("feed")
    object Stats : Screen("stats")
    object Profile : Screen("profile") // Was Garden
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            AppTopBar(
                onInfoClick = { showInfoDialog = true },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MAIN_PAGER,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.MAIN_PAGER) {
                MainPagerScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }

        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainPagerScreen() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HomeScreen()
                1 -> FeedScreen()
                2 -> StatsScreen()
                3 -> ProfileScreen() // Was CustomizeScreen
            }
        }
        LiquidGlassBottomNavigation(
            pagerState = pagerState,
            onTabSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(onInfoClick: () -> Unit, onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Information",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About Mindful Growth") },
        text = { Text("This app is designed to help you focus and build better habits.\n\nPrivacy Policy: [Link]\nTerms of Service: [Link]") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
