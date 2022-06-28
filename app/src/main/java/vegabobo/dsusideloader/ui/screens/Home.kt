package vegabobo.dsusideloader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.Toggles
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.cards.*
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.viewmodel.HomeViewModel

@Composable
fun Home(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    val userdataCard = homeViewModel.userdataCard
    val imageSizeCard = homeViewModel.imageSizeCard
    val installationCard = homeViewModel.installationCard
    val installationDialogVisibility = homeViewModel.installationDialog
    val gsiDsu = homeViewModel.gsiInstallation

    if (installationCard.isTextEmpty())
        installationCard.setText(stringResource(id = R.string.select_file))

    val context = LocalContext.current

    if (installationDialogVisibility.isEnabled())
        ConfirmInstallationDialog(
            gsiDsuObject = gsiDsu,
            onClickConfirm = { homeViewModel.onConfirmInstallationDialog(context) },
            onClickCancel = { homeViewModel.onCancelInstallationDialog() }
        )

    ApplicationScreen(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = it,
            ) {
                navController.navigate(Destinations.Settings)
            }
        }) {
        InstallationCard(
            onClickInstall = { homeViewModel.onClickInstall() },
            onClickClear = { homeViewModel.onClickClear() },
            onClickTextField = { homeViewModel.onClickSelectFile() },
            textFieldText = installationCard.getText(),
            isError = installationCard.isError(),
            isInstallable = installationCard.isInstallable(),
            isEnabled = installationCard.isEnabled()
        )
        Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
        UserdataCard(
            value = userdataCard.getText(),
            isToggleEnabled = userdataCard.isEnabled(),
            onCheckedChange = { homeViewModel.onTouchToggle(Toggles.USERDATA) },
            onValueChange = { homeViewModel.updateUserdataSize(it) },
        )
        Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
        ImageSizeCard(
            value = imageSizeCard.getText(),
            isToggleEnabled = imageSizeCard.isEnabled(),
            onCheckedChange = { homeViewModel.onTouchToggle(Toggles.IMGSIZE) },
            onValueChange = { homeViewModel.updateImageSize(it) }
        )
        Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
        DsuInfoCard()
    }

}