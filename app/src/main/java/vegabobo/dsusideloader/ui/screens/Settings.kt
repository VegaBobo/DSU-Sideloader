package vegabobo.dsusideloader.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle
import vegabobo.dsusideloader.viewmodel.SettingsViewModel

@Composable
fun Settings(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    ApplicationScreen(
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.preferences),
                icon = null,
                scrollBehavior = it,
                showBackButton = true,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    ) {

        val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.debug_installation),
            icon = Icons.Outlined.BugReport,
            description = stringResource(
                id = R.string.debug_installation_text
            ),
            showToggle = true,
            isChecked = uiState.debugInstallation,
            onClick = { settingsViewModel.toggleInstDebug(!it) },
        )
        PreferenceItem(
            title = "Unmount sdcard",
            description = "Avoid allocation on sdcard by temporary unmounting it",
            icon = Icons.Outlined.SdCard,
            showToggle = true,
            isChecked = uiState.umountSd,
            onClick = { settingsViewModel.toggleUmountSd(!it) },
        )
        PreferenceItem(
            title = stringResource(id = R.string.keep_screen_on),
            icon = Icons.Outlined.Smartphone,
            showToggle = true,
            isChecked = uiState.keepScreenOn,
            onClick = { settingsViewModel.toggleKeepScreenOn(!it) },
        )
        Title(title = stringResource(id = R.string.other))
        PreferenceItem(
            title = stringResource(id = R.string.op_mode),
            icon = Icons.Outlined.Build,
            description = uiState.operationMode
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = "Credits and more..",
            icon = Icons.Outlined.Info,
            onClick = {}
        )
    }

}