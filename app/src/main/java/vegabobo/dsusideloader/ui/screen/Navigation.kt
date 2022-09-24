package vegabobo.dsusideloader.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vegabobo.dsusideloader.ui.screen.about.AboutScreen
import vegabobo.dsusideloader.ui.screen.adb.AdbScreen
import vegabobo.dsusideloader.ui.screen.home.Home
import vegabobo.dsusideloader.ui.screen.libraries.LibrariesScreen
import vegabobo.dsusideloader.ui.screen.settings.Settings

object Destinations {
    const val Homepage = "home"
    const val Preferences = "preferences"
    const val ADBInstallation = "adb_installation"
    const val About = "about"
    const val Libraries = "libraries"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.Homepage) {
        composable(Destinations.Homepage) { Home(navController) }
        composable(Destinations.Preferences) { Settings(navController) }
        composable(Destinations.ADBInstallation) { AdbScreen(navController) }
        composable(Destinations.About) { AboutScreen(navController) }
        composable(Destinations.Libraries) { LibrariesScreen(navController) }
    }
}