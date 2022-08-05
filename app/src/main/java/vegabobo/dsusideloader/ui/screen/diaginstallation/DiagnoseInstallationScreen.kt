package vegabobo.dsusideloader.ui.screen.diaginstallation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.Dialog
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.fab.DiagnosticFab
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.util.LauncherAcResult
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun DiagnoseInstallationScreen(
    navController: NavController,
    diagInstViewModel: DiagnoseInstallationViewModel = hiltViewModel()
) {

    val uiState by diagInstViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.navigateUp) {
        diagInstViewModel.onClickConfirmBack(false)
        navController.navigateUp()
    }

    BackHandler {
        diagInstViewModel.release()
        navController.navigateUp()
    }

    val saveLogsResult = LauncherAcResult {
            diagInstViewModel.onClickSaveLogSuccess(it)
    }

    when (uiState.isShowingDialog) {
        DiagDialog.ERROR_EXTERNAL_SDCARD_ALLOC -> {
            Dialog(
                title = stringResource(id = R.string.allocation_error_title),
                text = stringResource(id = R.string.allocation_error_description),
                confirmText = stringResource(id = R.string.allocation_error_action),
                cancelText = stringResource(id = R.string.cancel),
                onClickConfirm = { diagInstViewModel.onClickEjectAndTryAgain() },
                onClickCancel = { diagInstViewModel.dismissDialog() }
            )
        }
        DiagDialog.ERROR_NO_AVAIL_STORAGE -> {
            Dialog(
                title = stringResource(id = R.string.storage_error_title),
                text = stringResource(id = R.string.storage_error_description),
                confirmText = stringResource(id = R.string.ok),
                onClickConfirm = { diagInstViewModel.dismissDialog() },
            )
        }
        DiagDialog.ERROR_F2FS_WRONG_PATH -> {
            Dialog(
                title = stringResource(id = R.string.filesystem_error_title),
                text = stringResource(id = R.string.filesystem_error_description),
                confirmText = stringResource(id = R.string.ok),
                onClickConfirm = { diagInstViewModel.dismissDialog() },
            )
        }
        DiagDialog.ERROR_SELINUX_A10 -> {
            Dialog(
                title = stringResource(id = R.string.selinux_error_title),
                text = stringResource(id = R.string.selinux_error_description),
                confirmText = stringResource(id = R.string.selinux_error_action),
                cancelText = stringResource(id = R.string.cancel),
                onClickConfirm = { diagInstViewModel.onClickSetPermissiveAndTryAgain() },
                onClickCancel = { diagInstViewModel.dismissDialog() }
            )
        }
        DiagDialog.ERROR_EXTENTS -> {
            Dialog(
                title = stringResource(id = R.string.extents_error_title),
                text = stringResource(id = R.string.extents_error_description),
                confirmText = stringResource(id = R.string.ok),
                onClickConfirm = { diagInstViewModel.dismissDialog() },
            )
        }
        DiagDialog.EXIT -> {
            Dialog(
                title = stringResource(id = R.string.debug_installation),
                text = stringResource(id = R.string.return_to_home),
                confirmText = stringResource(id = R.string.yes),
                cancelText = stringResource(id = R.string.no),
                onClickConfirm = { diagInstViewModel.onClickConfirmBack(true) },
                onClickCancel = { diagInstViewModel.dismissDialog() }
            )
        }
        else -> {}
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
                onClickIcon = { navController.navigate(Destinations.Preferences) },
                onClickBackButton = { diagInstViewModel.onClickConfirmBack(true) }
            )
        },
        content = {
            Surface {
                SimpleCard(
                    modifier = Modifier.fillMaxHeight(),
                    text = uiState.installationLogs,
                    textScrollable = true
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                DiagnosticFab(
                    fabAction = uiState.fabAction,
                    onClickStartInstallation = { diagInstViewModel.onClickStartInstallation() },
                    onClickSaveLogs = {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TITLE, "logs")
                        saveLogsResult.launch(intent)
                    },
                    onLongClickResetInstall = { diagInstViewModel.onLongClickResetInstall() }
                )
            }
        }
    )
}