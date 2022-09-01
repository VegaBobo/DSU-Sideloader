package vegabobo.dsusideloader.ui.screen.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.ActivityAction
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.cards.installation.InstallationCard
import vegabobo.dsusideloader.ui.cards.warnings.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.CancelDialog
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.ui.dialogs.DiscardDSUDialog
import vegabobo.dsusideloader.ui.dialogs.ImageSizeWarningDialog
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.util.KeepScreenOn
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

object HomeLinks {
    const val DSU_LEARN_MORE = "https://developer.android.com/topic/dsu"
    const val DSU_DOCS = "https://source.android.com/devices/tech/ota/dynamic-system-updates"
}

@Composable
fun Home(
    navController: NavController,
    activityRequest: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    if (uiState.shouldKeepScreenOn)
        KeepScreenOn()

    LaunchedEffect(Unit) {
        homeViewModel.homeViewAction.collectLatest {
            when (it) {
                HomeViewAction.NAVIGATE_TO_ADB_SCREEN ->
                    navController.navigate(Destinations.ADBInstallation)
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
                isCustomImageSize = homeViewModel.session.userSelection.isCustomImageSize(),
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
            Box(modifier = Modifier.animateContentSize()) {
                when (uiState.additionalCard) {
                    AdditionalCard.NO_DYNAMIC_PARTITIONS ->
                        UnsupportedCard { activityRequest(ActivityAction.FINISH_APP) }
                    AdditionalCard.SETUP_STORAGE ->
                        SetupStorage { homeViewModel.takeUriPermission(it) }
                    AdditionalCard.UNAVAIABLE_STORAGE ->
                        StorageWarningCard { homeViewModel.overrideUnavaiableStorage() }
                    AdditionalCard.MISSING_READ_LOGS_PERMISSION ->
                        RequiresLogPermissionCard(
                            onClickGrant =  { homeViewModel.grantReadLogs() },
                            onClickRefuse = { homeViewModel.refuseReadLogs() }
                        )
                    AdditionalCard.GRANTING_READ_LOGS_PERMISSION ->
                        GrantingPermissionCard()
                    else -> {}
                }
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
                    onClickViewLogs = { navController.navigate(Destinations.Logcat) }
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
                    onClickViewDocs = { uriHandler.openUri(HomeLinks.DSU_DOCS) },
                    onClickLearnMore = { uriHandler.openUri(HomeLinks.DSU_LEARN_MORE) },
                )
            }
        })
}