package vegabobo.dsusideloader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.CancelDialog
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
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

    if (uiState.showCancelDialog) {
        CancelDialog(
            onClickConfirm = { homeViewModel.onClickCancelInstallationButton() },
            onClickCancel = { homeViewModel.onDismissCancelDialog() },
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
        }) {
        if (uiState.showUnsupportedCard) {
            UnsupportedCard { homeViewModel.finishAppAction() }
        } else {
            if (uiState.showSetupStorageCard)
                NoAvailStorageCard { homeViewModel.onSetupStorageAction() }
            if (uiState.showLowStorageCard)
                StorageWarningCard {
                    homeViewModel.showNoAvailStorageCard(false)
                }
        }
        if (homeViewModel.isDeviceCompatible()) {
            InstallationCard(
                onClickInstall = { homeViewModel.onClickInstallOrCancelButton(uiState.isInstalling) },
                onClickClear = { homeViewModel.onClickClearButton() },
                onClickTextField = { homeViewModel.onSelectFileAction() },
                textFieldText = uiState.installationFieldText,
                isError = false,
                isInstallable = uiState.isInstallable,
                isEnabled = uiState.isInstallationFieldEnabled,
                installationText = uiState.installationText,
                installationProgressBar = uiState.installationProgress,
                isInstalling = uiState.isInstalling
            )
            UserdataCard(
                value = uiState.userdataFieldText,
                isToggleEnabled = uiState.isCustomUserdataSelected,
                onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                onValueChange = { homeViewModel.updateUserdataSize(it) },
            )
            ImageSizeCard(
                textFieldValue = uiState.imageSizeFieldText,
                isToggleEnabled = uiState.isCustomImageSizeSelected,
                onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                onValueChange = { homeViewModel.updateImageSize(it) }
            )
            DsuInfoCard()
        }
    }

}