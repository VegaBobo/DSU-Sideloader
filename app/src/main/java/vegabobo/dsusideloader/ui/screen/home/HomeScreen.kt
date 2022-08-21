package vegabobo.dsusideloader.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.ActivityAction
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.CancelDialog
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.ui.dialogs.DiscardDSUDialog
import vegabobo.dsusideloader.ui.dialogs.ImageSizeWarningDialog
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.util.KeepScreenOn
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun Home(
    navController: NavController,
    activityRequest: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.shouldKeepScreenOn)
        KeepScreenOn()

    LaunchedEffect(Unit) {
        homeViewModel.homeViewAction.collectLatest {
            when (it) {
                HomeViewAction.NAVIGATE_TO_ADB_SCREEN ->
                    navController.navigate(Destinations.ADBInstallation)
                HomeViewAction.NAVIGATE_TO_LOGCAT_SCREEN ->
                    navController.navigate(Destinations.Logcat)
                else -> {}
            }
            homeViewModel.resetViewAction()
        }
    }

    when (uiState.dialogDisplay) {
        DialogDisplay.CONFIRM_INSTALLATION ->
            ConfirmInstallationDialog(
                filename = homeViewModel.obtainSelectedFilename(),
                userdata = homeViewModel.session.userSelection.getUserDataSizeAsGB(),
                fileSize = homeViewModel.session.userSelection.userSelectedImageSize,
                onClickConfirm = { homeViewModel.onConfirmInstallationDialog() },
                onClickCancel = { homeViewModel.dismissDialog() }
            )
        DialogDisplay.CANCEL_INSTALLATION ->
            CancelDialog(
                onClickConfirm = { homeViewModel.onClickCancelInstallationButton() },
                onClickCancel = { homeViewModel.dismissDialog() },
            )
        DialogDisplay.IMAGESIZE_WARNING ->
            ImageSizeWarningDialog(
                onClickConfirm = { homeViewModel.dismissDialog() },
                onClickCancel = { homeViewModel.onCheckImageSizeCard() }
            )
        DialogDisplay.DISCARD_DSU ->
            DiscardDSUDialog(
                onClickConfirm = { homeViewModel.onClickDiscardGsi() },
                onClickCancel = { homeViewModel.dismissDialog() }
            )
        else -> {}
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = it,
                onClickIcon = { navController.navigate(Destinations.Preferences) }
            )
        },
        content = {
            when (uiState.additionalCard) {
                AdditionalCard.NO_DYNAMIC_PARTITIONS ->
                    UnsupportedCard { activityRequest(ActivityAction.FINISH_APP) }
                AdditionalCard.SETUP_STORAGE ->
                    SetupStorage { homeViewModel.takeUriPermission(it) }
                AdditionalCard.UNAVAIABLE_STORAGE ->
                    StorageWarningCard { homeViewModel.overrideUnavaiableStorage() }
                else -> {}
            }

            if (uiState.canInstall) {
                InstallationCard(
                    uiState = uiState.installationCard,
                    onClickInstall = { homeViewModel.onClickInstall() },
                    onClickUnmountSdCardAndRetry = { homeViewModel.onClickUnmountSdCardAndRetry() },
                    onClickSetSeLinuxPermissive = { homeViewModel.onClickSetSeLinuxPermissive() },
                    onClickRetryInstallation = { homeViewModel.onClickRetryInstallation() },
                    onClickClear = { homeViewModel.resetInstallationCard() },
                    onSelectFileSuccess = { homeViewModel.onFileSelectionResult(it) },
                    onClickCancelInstallation = { homeViewModel.onClickCancel() },
                    onClickDiscardInstalledGsiAndInstall = { homeViewModel.onClickDiscardGsiAndStartInstallation() },
                    onClickDiscardDsu = { homeViewModel.showDiscardDialog() },
                    onClickRebootToDynOS = { homeViewModel.onClickRebootToDynOS() },
                    onClickViewLogs = { homeViewModel.onClickViewLogs() }
                )
                UserdataCard(
                    isEnabled = uiState.isInstalling(),
                    uiState = uiState.userDataCard,
                    onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                    onValueChange = { homeViewModel.updateUserdataSize(it) },
                )
                ImageSizeCard(
                    isEnabled = uiState.isInstalling(),
                    uiState = uiState.imageSizeCard,
                    onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                    onValueChange = { homeViewModel.updateImageSize(it) }
                )
                DsuInfoCard(
                    onClickViewDocs = { homeViewModel.onClickViewDocs() },
                    onClickLearnMore = { homeViewModel.onClickLearnMore() },
                )
            }
        })
}