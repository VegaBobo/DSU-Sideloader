package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.ui.snackbar.InstallationSnackBar
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle
import vegabobo.dsusideloader.viewmodel.HomeViewModel

@Composable
fun Home(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val gsiDsu = homeViewModel.gsiToBeInstalled

    if (uiState.showInstallationDialog)
        ConfirmInstallationDialog(
            GSI = gsiDsu,
            onClickConfirm = { homeViewModel.onConfirmInstallationDialog() },
            onClickCancel = { homeViewModel.onCancelInstallationDialog() }
        )

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = it,
            ) {
                navController.navigate(Destinations.Settings)
            }
        },
        outsideContent = {
            AnimatedVisibility(
                visible = uiState.isInstalling,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                InstallationSnackBar(
                    paddingValues = it,
                    text = uiState.installationText,
                    onClickButton = { },
                    //showProgressIndicator = !homeViewModel.installationProgress.isFinished(),
                    showProgressIndicator = false,
                    textButton = if (!uiState.isInstalling)
                        stringResource(id = R.string.close) else stringResource(id = R.string.cancel)
                )
            }
        }) {
        if (uiState.showUnsupportedCard) {
            UnsupportedCard(onClickButton = { homeViewModel.finishAppAction() })
        } else {
            if (uiState.showSetupStorageCard)
                AttentionCard(onClick = {
                    homeViewModel.onSetupStorageAction()
                })
            if (uiState.showLowStorageCard)
                StorageWarningCard {
                    homeViewModel.showNoAvailStorageCard(false)
                }
        }
        if (homeViewModel.isDeviceCompatible()) {
            InstallationCard(
                onClickInstall = { homeViewModel.onClickInstallButton() },
                onClickClear = { homeViewModel.onClickClearButton() },
                onClickTextField = { homeViewModel.onSelectFileAction() },
                textFieldText = uiState.installationFieldText,
                isError = false,
                isInstallable = uiState.isInstallable,
                isEnabled = uiState.isInstallationFieldEnabled
            )
            UserdataCard(
                value = uiState.userdataFieldText,
                isToggleEnabled = uiState.isCustomUserdataSelected,
                onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                onValueChange = { homeViewModel.updateUserdataSize(it) },
            )
            ImageSizeCard(
                value = uiState.imageSizeFieldText,
                isToggleEnabled = uiState.isCustomImageSizeSelected,
                onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                onValueChange = { homeViewModel.updateImageSize(it) }
            )
            DsuInfoCard()
        }
    }

}