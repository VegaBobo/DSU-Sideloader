package vegabobo.dsusideloader.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vegabobo.dsusideloader.ui.Destinations.Homepage
import vegabobo.dsusideloader.ui.Destinations.Settings
import vegabobo.dsusideloader.ui.screens.Home
import vegabobo.dsusideloader.ui.screens.Settings
import vegabobo.dsusideloader.viewmodel.HomeViewModel
import vegabobo.dsusideloader.viewmodel.SettingsViewModel

object Destinations {
    const val Homepage = "home"
    const val Settings = "settings"
}

@Composable
fun Navigation(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Homepage) {
        composable(Homepage) { Home(navController, homeViewModel) }
        composable(Settings) { Settings(navController, settingsViewModel) }
    }
}