package vegabobo.dsusideloader.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vegabobo.dsusideloader.ui.screen.adb.AdbScreen
import vegabobo.dsusideloader.ui.screen.logcat.LogcatScreen
import vegabobo.dsusideloader.ui.screen.home.Home
import vegabobo.dsusideloader.ui.screen.settings.Settings

object Destinations {
    const val Homepage = "home"
    const val Preferences = "preferences"
    const val ADBInstallation = "adb_installation"
    const val Logcat = "logcat"
}

@Composable
fun Navigation(
    activityRequest: (Int) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.Homepage) {
        composable(Destinations.Homepage) { Home(navController, activityRequest) }
        composable(Destinations.Preferences) { Settings(navController) }
        composable(Destinations.Logcat) { LogcatScreen(navController) }
        composable(Destinations.ADBInstallation) { AdbScreen(navController) }
    }
}