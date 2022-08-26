package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.ui.screen.Destinations
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

        LaunchedEffect(Unit) {
            settingsViewModel.settingsViewAction.collectLatest {
                when (it) {
                    SettingsViewAction.NAVIGATE_TO_ABOUT -> navController.navigate(Destinations.About)
                    else -> {}
                }
                settingsViewModel.resetViewAction()
            }
        }

        if (uiState.isShowingBuiltinInstallerDialog)
            Dialog(
                title = stringResource(id = R.string.use_builin_installer),
                text = stringResource(id = R.string.testing_feature),
                confirmText = stringResource(id = R.string.got_it),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = { settingsViewModel.toggleBuiltinInstaller(false) },
                onClickConfirm = { settingsViewModel.updateInstallerDialogState(false) }
            )

        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.use_builin_installer),
            description =
            if (uiState.isRoot) stringResource(id = R.string.use_builin_installer_desc)
            else stringResource(R.string.requires_root),
            showToggle = true,
            isEnabled = uiState.isRoot,
            isChecked = uiState.useBuiltinInstaller,
            onClick = { settingsViewModel.toggleBuiltinInstaller(!it) },
        )
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
            description = settingsViewModel.checkOperationMode()
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_text),
            onClick = { settingsViewModel.navigateToAbout() }
        )
    }

}