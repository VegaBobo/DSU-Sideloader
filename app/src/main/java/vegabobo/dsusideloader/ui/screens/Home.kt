package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.Toggles
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.Dialog
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.components.cards.GenericCard
import vegabobo.dsusideloader.ui.components.cards.InfoCard
import vegabobo.dsusideloader.ui.components.cards.InstallationCard
import vegabobo.dsusideloader.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState()
    )

    val userdataCard = homeViewModel.userdataCard
    val imageSizeCard = homeViewModel.imageSizeCard
    val installationCard = homeViewModel.installationCard
    val installationDialogVisibility = homeViewModel.installationDialog
    val gsiDsu = homeViewModel.gsiInstallation

    if (installationCard.text.value.isEmpty())
        homeViewModel.installationCard.text.value = stringResource(id = R.string.select_file)

    val context = LocalContext.current

    if (installationDialogVisibility.isEnabled.value)
        Dialog(
            title = stringResource(id = R.string.info),
            text = stringResource(
                id = R.string.installation_details,
                installationCard.text.value,
                gsiDsu.userdataSize,
                if (gsiDsu.fileSize == -1L) stringResource(id = R.string.auto) else gsiDsu.fileSize,
            ),
            confirmText = stringResource(id = R.string.proceed),
            cancelText = stringResource(id = R.string.cancel),
            onClickConfirm = { homeViewModel.onConfirmDialog() },
            onClickCancel = { homeViewModel.onCancelDialog() })

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = scrollBehavior,
            ) {
                navController.navigate(Destinations.Settings)
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(14.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InstallationCard(
                    onClickInstall = { homeViewModel.onClickInstall() },
                    onClickClear = { homeViewModel.onClickClear() },
                    onClickTextField = { homeViewModel.onClickSelectFile() },
                    textFieldText = installationCard.text.value,
                    isError = installationCard.isError.value,
                    isInstallable = installationCard.isInstallable.value,
                    isEnabled = installationCard.isEnabled.value
                )
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                GenericCard(
                    cardTitle = stringResource(id = R.string.userdata_size_ct),
                    textFieldTitle = stringResource(id = R.string.userdata_size_n),
                    addToggle = true,
                    isToggleEnabled = userdataCard.isEnabled.value,
                    isError = false,
                    onCheckedChange = {
                        homeViewModel.onTouchToggle(Toggles.USERDATA_TOGGLE)
                    },
                    value = userdataCard.text.value,
                    onValueChange = { homeViewModel.updateUserdataSize(it) },
                )
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                GenericCard(
                    cardTitle = stringResource(id = R.string.image_size),
                    textFieldTitle = stringResource(id = R.string.image_size_custom),
                    addToggle = true,
                    isToggleEnabled = imageSizeCard.isEnabled.value,
                    isError = false,
                    onCheckedChange = {
                        homeViewModel.onTouchToggle(Toggles.IMGSIZE_TOGGLE)
                    },
                    value = imageSizeCard.text.value,
                    onValueChange = { homeViewModel.updateImageSize(it) }
                )
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                InfoCard()
            }
        }

    )

}