package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.runtime.Composable
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
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    ApplicationScreen(
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.preferences),
                scrollBehavior = it,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    ) {
        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.builtin_installer),
            description =
            if (uiState.isRoot) stringResource(id = R.string.builtin_installer_description)
            else stringResource(R.string.requires_root),
            showToggle = true,
            isEnabled = uiState.isRoot,
            isChecked = uiState.preferences[AppPrefs.USE_BUILTIN_INSTALLER]!!,
            onClick = {
                settingsViewModel.updateInstallerSheetState(!it)
                settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, !it)
            },
        )
        PreferenceItem(
            title = stringResource(id = R.string.unmount_sd_title),
            description = stringResource(id = R.string.unmount_sd_description),
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
            title = stringResource(id = R.string.operation_mode),
            description = settingsViewModel.checkOperationMode()
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_description),
            onClick = { navController.navigate(Destinations.About) }
        )
    }

    if (uiState.isShowingBuiltinInstallerSheet)
        DialogLikeBottomSheet(
            title = stringResource(id = R.string.experimental_feature),
            icon = Icons.Outlined.NewReleases,
            text = stringResource(id = R.string.experimental_feature_description),
            confirmText = stringResource(id = R.string.yes),
            cancelText = stringResource(id = R.string.cancel),
            onClickCancel = {
                settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, false)
                settingsViewModel.updateInstallerSheetState(false)
            },
            onClickConfirm = { settingsViewModel.updateInstallerSheetState(false) }
        )

}