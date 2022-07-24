package vegabobo.dsusideloader.ui.screens.home

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.ActivityAction
import vegabobo.dsusideloader.preparation.InstallationSteps
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.Dialog
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.CancelDialog
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.ui.util.KeepScreenOn
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun Home(
    navController: NavController,
    activityRequest: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    // ui state

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val gsiDsu = homeViewModel.gsiToBeInstalled

    // handle ActivityResult (setup storage, and file selection)

    val context = LocalContext.current

    val launcherSetupStorage =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                context.contentResolver.takePersistableUriPermission(
                    it.data!!.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                homeViewModel.onSetupStorageSuccess(it.data!!.data!!.toString())
            }
        }

    val launcherSelectFile =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val filename = FilenameUtils.queryName(context.contentResolver, it.data!!.data!!)
                homeViewModel.onSelectFileSuccessfully(it.data!!.data!!, filename)
            }
        }


    LaunchedEffect(key1 = Unit) {
        homeViewModel.adbInstallation.collectLatest {
            when (it.screenToOpen) {
                Destinations.Adb -> navController.navigate("${Destinations.Adb}/${homeViewModel.adbInstallation.value.installationCmd}")
                Destinations.RootDiagInstallation -> navController.navigate("${Destinations.RootDiagInstallation}/${homeViewModel.adbInstallation.value.installationCmd}")
            }
            homeViewModel.adbInstallation.value = InstallationNav()
        }
    }
    // UI
    LaunchedEffect(key1 = Unit) {
        homeViewModel.keepScreenOn()
    }
    if (uiState.keepScreenOn)
        KeepScreenOn()

    val installationText = when (uiState.installationStep) {
        InstallationSteps.COPYING_FILE -> stringResource(R.string.gz_copy)
        InstallationSteps.DECOMPRESSING_XZ -> stringResource(R.string.extracting_gzip)
        InstallationSteps.COMPRESSING_TO_GZ -> stringResource(R.string.compressing_img_to_gzip)
        InstallationSteps.DECOMPRESSING_GZIP -> stringResource(R.string.extracting_gzip)
        InstallationSteps.FINISHED -> stringResource(id = R.string.done)
        else -> stringResource(R.string.processing)
    }

    if (uiState.showInstallationDialog)
        ConfirmInstallationDialog(
            GSI = gsiDsu,
            onClickConfirm = { homeViewModel.onConfirmInstallationDialog() },
            onClickCancel = { homeViewModel.onCancelInstallationDialog() }
        )

    if (uiState.showCancelDialog) {
        CancelDialog(
            onClickConfirm = { homeViewModel.onClickCancelInstallationButton() },
            onClickCancel = { homeViewModel.onDismissCancelDialog() },
        )
    }

    if (uiState.showImageSizeDialog) {
        Dialog(
            title = stringResource(id = R.string.custom_image_size),
            text = stringResource(id = R.string.custom_image_size_warning),
            confirmText = stringResource(id = R.string.set_anyway),
            cancelText = stringResource(id = R.string.cancel),
            onClickConfirm = { homeViewModel.onClickConfirmImageSizeDialog() },
            onClickCancel = { homeViewModel.onClickCancelImageSizeDialog() }
        )
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = it,
                onClickIcon = { navController.navigate(Destinations.Settings) }
            )
        },
        content = {
            if (uiState.showUnsupportedCard) {
                UnsupportedCard { activityRequest(ActivityAction.FINISH_APP) }
            } else {
                if (uiState.showSetupStorageCard)
                    SetupStorage {
                        homeViewModel.onClickSetupStorage(launcherSetupStorage)
                    }
                if (uiState.showLowStorageCard)
                    StorageWarningCard {
                        homeViewModel.onClickIgnoreStorageWarning(false)
                    }
            }
            if (homeViewModel.isDeviceCompatible()) {
                val config = LocalConfiguration.current
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Row {
                    }
                }
                InstallationCard(
                    onClickInstall = { homeViewModel.onClickInstallOrCancelButton(uiState.isInstalling) },
                    onClickClear = { homeViewModel.onClickClearButton() },
                    onClickTextField = { homeViewModel.onSelectFileResult(launcherSelectFile) },
                    textFieldText = uiState.installationFieldText,
                    isError = false,
                    isInstallable = uiState.isInstallable,
                    isEnabled = uiState.isInstallationFieldEnabled,
                    installationText = installationText,
                    installationProgressBar = uiState.installationProgress,
                    isInstalling = uiState.isInstalling
                )
                UserdataCard(
                    value = uiState.userdataFieldText,
                    isError = uiState.isCustomUserdataError,
                    isToggleChecked = uiState.isCustomUserdataSelected,
                    isEnabled = !uiState.isInstalling,
                    maximumAllowedAlloc = uiState.maximumAllowedAlloc,
                    onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                    onValueChange = { homeViewModel.updateUserdataSize(it) },
                )
                ImageSizeCard(
                    textFieldValue = uiState.imageSizeFieldText,
                    isEnabled = !uiState.isInstalling,
                    isToggleChecked = uiState.isCustomImageSizeSelected,
                    onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                    onValueChange = { homeViewModel.updateImageSize(it) }
                )
                DsuInfoCard(
                    onClickViewDocs = { homeViewModel.onClickViewDocs(context) },
                    onClickLearnMore = { homeViewModel.onClickLearnMore(context) },
                )
            }
        })
}