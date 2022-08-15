package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.util.OperationModeUtils
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun Settings(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    ApplicationScreen(
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.preferences),
                scrollBehavior = it,
                showBackButton = true,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    ) {
        val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.unmount_sd),
            description = stringResource(id = R.string.unmount_sd_text),
            showToggle = true,
            isChecked = uiState.umountSd,
            onClick = { settingsViewModel.toggleUmountSd(!it) },
        )
        PreferenceItem(
            title = stringResource(id = R.string.keep_screen_on),
            showToggle = true,
            isChecked = uiState.keepScreenOn,
            onClick = { settingsViewModel.toggleKeepScreenOn(!it) },
        )

        Title(title = stringResource(id = R.string.other))
        PreferenceItem(
            title = stringResource(id = R.string.op_mode),
            description = OperationModeUtils.getOperationModeAsString(settingsViewModel.getOperationMode())
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_text),
            onClick = {}
        )
    }

}