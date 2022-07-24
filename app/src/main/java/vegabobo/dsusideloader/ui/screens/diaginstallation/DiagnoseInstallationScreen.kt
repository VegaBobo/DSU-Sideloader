package vegabobo.dsusideloader.ui.screens.diaginstallation

import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.Dialog
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiagnoseInstallationScreen(
    navController: NavController,
    cmd: String? = "",
    diagInstViewModel: DiagnoseInstallationViewModel = viewModel()
) {
    val command = String(Base64.decode(cmd, Base64.DEFAULT))
    diagInstViewModel.updateInstallationCmd(command)

    val uiState by diagInstViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.navigateUp) {
        diagInstViewModel.onClickConfirmBack(false)
        navController.navigateUp()
    }

    BackHandler {
        diagInstViewModel.release()
        navController.navigateUp()
    }

    when (uiState.isShowingDialog) {
        DiagDialog.ERROR_EXTERNAL_SDCARD_ALLOC -> {
            Dialog(
                title = "Allocation error",
                text = "GSI daemon is trying to allocate in external SD, but it fails for some reason (likely due sepolicy denials)" +
                        "\nTo fix that, you can install DSU Sideloader Magisk's module, or temporary eject sd to avoid this (you can set this option permanently in preferences).",
                confirmText = "Eject SD and retry",
                cancelText = "Cancel",
                onClickConfirm = { diagInstViewModel.onClickEjectAndTryAgain() },
                onClickCancel = { diagInstViewModel.onClickCancel() }
            )
        }
        DiagDialog.ERROR_NO_AVAIL_STORAGE -> {
            Dialog(
                title = "Storage error",
                text = "Android requires, at least 40% of free storage to proceed installation, free up some space or decrease userdata size, and try again.",
                confirmText = "Ok",
                onClickConfirm = { diagInstViewModel.onClickCancel() },
            )
        }
        DiagDialog.ERROR_F2FS_WRONG_PATH -> {
            Dialog(
                title = "Filesystem features not found",
                text = "Your kernel may be registering f2fs in different path, that one expected from GSI daemon.\n" +
                        "To fix that, you can try to install DSU Sideloader Magisk's module, or change kernel.",
                confirmText = "Ok",
                onClickConfirm = { diagInstViewModel.onClickCancel() },
            )
        }
        DiagDialog.ERROR_SELINUX_A10 -> {
            Dialog(
                title = "SELinux error",
                text = "Looks like SELinux is blocking GSI daemon to work due denials, this error is common devices running Android 10\n" +
                        "To fix that, you can try to install DSU Sideloader Magisk's module, or set SELinux to permissive.\n" +
                        "** Changing to permissive will weakens your device security.",
                confirmText = "Retry with permissive",
                cancelText = "Cancel",
                onClickConfirm = { diagInstViewModel.onClickSetPermissiveAndTryAgain() },
                onClickCancel = { diagInstViewModel.onClickCancel() }
            )
        }
        DiagDialog.ERROR_EXTENTS -> {
            Dialog(
                title = "Extents error",
                text = "On Android 10 devices, images/filesystem needs to have more than 512 extents.\n" +
                        "To fix that, you can try to install DSU Sideloader Magisk's module.",
                confirmText = "Ok",
                onClickConfirm = { diagInstViewModel.onClickCancel() },
            )
        }
        DiagDialog.EXIT -> {
            Dialog(
                title = stringResource(id = R.string.debug_installation),
                text = stringResource(id = R.string.return_to_home),
                confirmText = stringResource(id = R.string.yes),
                cancelText = stringResource(id = R.string.no),
                onClickConfirm = { diagInstViewModel.onClickConfirmBack(true) },
                onClickCancel = { diagInstViewModel.onClickCancel() }
            )
        }
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        columnContent = false,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.ready_to_install),
                scrollBehavior = it,
                showBackButton = true,
                onClickIcon = { navController.navigate(Destinations.Settings) },
                onClickBackButton = { diagInstViewModel.onClickConfirmBack(true) }
            )
        },
        content = {
            SimpleCard(
                modifier = Modifier.fillMaxHeight(),
                text = uiState.installationLogs,
                textScrollable = true
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                when (uiState.fabAction) {
                    FabAction.INSTALL -> {
                        FloatingActionButton(onClick = { diagInstViewModel.onClickStartInstallation() }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Launch icon",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(text = "Start installation")
                            }
                        }
                    }
                    FabAction.SAVE_LOGS -> {
                        FloatingActionButton(
                            onClick = { diagInstViewModel.onClickSaveLogs() },
                        ) {
                            Box(
                                modifier = Modifier
                                    .combinedClickable(onClick = { diagInstViewModel.onClickSaveLogs() },
                                        onLongClick = { diagInstViewModel.onLongClickResetInstall() })
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Save,
                                        contentDescription = "Save icon",
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(text = "Save log")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}