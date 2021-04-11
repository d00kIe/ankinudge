package com.teraculus.lingojournalandroid.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.teraculus.lingojournalandroid.ui.home.HomeScreen
import com.teraculus.lingojournalandroid.ui.navi.Screen
import com.teraculus.lingojournalandroid.ui.stats.StatsScreen


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Main(
    onActivityClick: (id: String) -> Unit,
    onOpenEditor: (id: String?) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val navController = rememberNavController()
    val screen = listOf(Screen.Home, Screen.Stats)

    LingoTheme {
        MainContent(navController,
            screen,
            onAddActivity = { onOpenEditor(null) },
            onActivityClick = onActivityClick,
            onOpenSettings = onOpenSettings)
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainContent(
    navController: NavHostController,
    screens: List<Screen>,
    onAddActivity: () -> Unit,
    onActivityClick: (id: String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            val screen = getCurrentScreenFrom(screens = screens,
                route = getCurrentRoute(navController = navController))
            val elevation =
                if (!MaterialTheme.colors.isLight || screen == Screen.Stats) 0.dp else AppBarDefaults.TopAppBarElevation
            TopAppBar(
                title = {
                    if (screen != null) {
                        Text(stringResource(id = screen.resourceId),
                            modifier = Modifier.padding(start = 24.dp),
                            style = MaterialTheme.typography.h6)
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                actions = {

                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                })
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                elevation = 8.dp,
                cutoutShape = CircleShape
            ) {
                val currentRoute = getCurrentRoute(navController)
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentRoute == screen.route,
                        alwaysShowLabel = false,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo = navController.graph.startDestination
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val currentRoute = getCurrentRoute(navController = navController)
            if (currentRoute == "home" || currentRoute == "stats") {
                FloatingActionButton(onClick = onAddActivity) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        Modifier.size(32.dp)
                    )
                }
            }
        }
    ) {

        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen(onItemClick = onActivityClick) }
            composable(Screen.Stats.route) { StatsScreen(onItemClick = onActivityClick) }
        }
    }
}

@Composable
private fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}

@Composable
private fun getCurrentScreenFrom(screens: List<Screen>, route: String?): Screen? {
    return screens.find { it.route == route }
}