package vegabobo.dsusideloader.ui.screen.adb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.CopyableTextCard
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.screen.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdbScreen(
    navigate: (String) -> Unit,
    adbViewModel: AdbViewModel = hiltViewModel(),
) {
    val scriptPath = adbViewModel.obtainScriptPath()

    val startInstallationCommand = "sh \"$scriptPath\""
    val startInstallationCommandAdb = "adb shell $startInstallationCommand"
    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.installation),
                scrollBehavior = it,
                onClickIcon = { navigate(Destinations.Preferences) },
                onClickBackButton = { navigate(Destinations.Up) },
            )
        },
        content = {
            Text(text = stringResource(id = R.string.adb_how_to_adb_shell))
            CopyableTextCard(text = startInstallationCommandAdb)
            Text(text = stringResource(id = R.string.adb_how_to_shell))
            CopyableTextCard(text = startInstallationCommand)
            Text(text = stringResource(id = R.string.adb_how_to_done))
        },
    )
}
