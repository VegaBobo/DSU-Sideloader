package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun Settings(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        settingsViewModel.checkIfRootIsAvail()
    }

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

        if (uiState.isShowingBuiltinInstallerDialog)
            Dialog(
                title = stringResource(id = R.string.use_builin_installer),
                icon = Icons.Outlined.NewReleases,
                text = stringResource(id = R.string.testing_feature),
                confirmText = stringResource(id = R.string.got_it),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = { settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, false) },
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
            isChecked = uiState.preferences[AppPrefs.USE_BUILTIN_INSTALLER]!!,
            onClick = {
                settingsViewModel.updateInstallerDialogState(!it)
                settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, !it)
            },
        )
        PreferenceItem(
            title = stringResource(id = R.string.unmount_sd),
            description = stringResource(id = R.string.unmount_sd_text),
            showToggle = true,
            isChecked = uiState.preferences[AppPrefs.UMOUNT_SD]!!,
            onClick = { settingsViewModel.togglePreference(AppPrefs.UMOUNT_SD, !it) },
        )
        PreferenceItem(
            title = stringResource(id = R.string.keep_screen_on),
            showToggle = true,
            isChecked = uiState.preferences[AppPrefs.KEEP_SCREEN_ON]!!,
            onClick = { settingsViewModel.togglePreference(AppPrefs.KEEP_SCREEN_ON, !it) },
        )

        Title(title = stringResource(id = R.string.other))
        PreferenceItem(
            title = stringResource(id = R.string.op_mode),
            description = settingsViewModel.checkOperationMode()
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_text),
            onClick = { navController.navigate(Destinations.About) }
        )
    }

}