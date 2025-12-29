package com.example.mindfulgrowth.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindfulgrowth.ui.screens.customize.CustomizeScreen
import com.example.mindfulgrowth.ui.screens.home.HomeScreen
import com.example.mindfulgrowth.ui.screens.settings.SettingsScreen
import com.example.mindfulgrowth.ui.screens.stats.StatsScreen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Stats : Screen("stats", "Stats", Icons.Filled.AutoGraph)
    object Customize : Screen("customize", "Customize", Icons.Filled.Palette)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun MindfulGrowthApp() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )

        BottomNavigationBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToSettings = { navController.navigate(Screen.Settings.route) })
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Customize.route) {
            CustomizeScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Home,
        Screen.Stats,
        Screen.Customize,
        Screen.Settings
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color(0xFF1F2121), // SurfaceColor
        contentColor = Color(0xFFF5F5F5),   // TextPrimary
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFD4AF37), // GoldPrimary
                    unselectedIconColor = Color(0xFFA7A9A9)  // TextSecondary
                )
            )
        }
    }
}
