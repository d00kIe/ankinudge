package com.teraculus.lingojournalandroid.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.ui.components.AddActivityDialog
import com.teraculus.lingojournalandroid.ui.home.HomeScreen
import com.teraculus.lingojournalandroid.ui.navi.Screen


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Main() {
    val navController = rememberNavController()
    val screen = listOf(Screen.Home, Screen.Calendar, Screen.Stats, Screen.Settings)
    var showAddActivityDialog by rememberSaveable { mutableStateOf(false) }
    var activityId: String? by rememberSaveable { mutableStateOf(null) }

    LingoTheme() {
        Main(navController,screen, onAddActivity = { showAddActivityDialog = true }, onActivityClick = { activityId = it; showAddActivityDialog = true })
        AddActivityDialog(
            show = showAddActivityDialog,
            onDismiss = { showAddActivityDialog = false; activityId = null },
            id = activityId
        )
    }
}

@Composable
fun Main(navController: NavHostController, screens: List<Screen>, onAddActivity: () -> Unit, onActivityClick: (id: String) -> Unit) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentRoute == screen.route,
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
        floatingActionButton = {
            FloatingActionButton(onClick = onAddActivity) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = ""
                )
            }
        }
    ) {

        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen(onItemClick = onActivityClick) }
            composable(Screen.Calendar.route) { HomeScreen(onItemClick = onActivityClick) }
            composable(Screen.Stats.route) { HomeScreen(onItemClick = onActivityClick) }
            composable(Screen.Settings.route) { HomeScreen(onItemClick = onActivityClick) }
        }
    }
}