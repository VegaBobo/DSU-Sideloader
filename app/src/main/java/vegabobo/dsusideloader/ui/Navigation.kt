package vegabobo.dsusideloader.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import vegabobo.dsusideloader.ui.Destinations.Adb
import vegabobo.dsusideloader.ui.Destinations.Homepage
import vegabobo.dsusideloader.ui.Destinations.Settings
import vegabobo.dsusideloader.ui.screens.adb.AdbScreen
import vegabobo.dsusideloader.ui.screens.home.Home
import vegabobo.dsusideloader.ui.screens.settings.Settings

object Destinations {
    const val Homepage = "home"
    const val Settings = "settings"
    const val Adb = "adb"
}

@Composable
fun Navigation(
    activityRequest: (Int) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Homepage) {
        composable(Homepage) { Home(navController, activityRequest) }
        composable(Settings) { Settings(navController) }
        composable(
            route = "$Adb/{cmd}",
            arguments = listOf(navArgument("cmd") { type = NavType.StringType })
        ) { AdbScreen(navController, it.arguments?.getString("cmd")) }
    }
}