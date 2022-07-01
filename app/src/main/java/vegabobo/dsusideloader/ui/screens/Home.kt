package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.model.Toggles
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.cards.DsuInfoCard
import vegabobo.dsusideloader.ui.cards.ImageSizeCard
import vegabobo.dsusideloader.ui.cards.InstallationCard
import vegabobo.dsusideloader.ui.cards.UserdataCard
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.dialogs.ConfirmInstallationDialog
import vegabobo.dsusideloader.ui.snackbar.InstallationSnackBar
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
            GSI = gsiDsu,
            onClickConfirm = { homeViewModel.onConfirmInstallationDialog(context) },
            onClickCancel = { homeViewModel.onCancelInstallationDialog() }
        )

    ApplicationScreen(
        modifier = Modifier.padding(18.dp),
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
                visible = homeViewModel.isInstalling.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                InstallationSnackBar(
                    paddingValues = it,
                    text = homeViewModel.installationProgress.getProgress(context),
                    onClickButton = {
                        homeViewModel.isInstalling.value =
                            !homeViewModel.isInstalling.value
                    },
                    showProgressIndicator = !homeViewModel.installationProgress.isFinished(),
                    textButton = if (homeViewModel.installationProgress.isFinished())
                        stringResource(id = R.string.close) else stringResource(id = R.string.cancel)
                )
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
        UserdataCard(
            value = userdataCard.getText(),
            isToggleEnabled = userdataCard.isEnabled(),
            onCheckedChange = { homeViewModel.onTouchToggle(Toggles.USERDATA) },
            onValueChange = { homeViewModel.updateUserdataSize(it) },
        )
        ImageSizeCard(
            value = imageSizeCard.getText(),
            isToggleEnabled = imageSizeCard.isEnabled(),
            onCheckedChange = { homeViewModel.onTouchToggle(Toggles.IMGSIZE) },
            onValueChange = { homeViewModel.updateImageSize(it) }
        )
        DsuInfoCard()
    }

}