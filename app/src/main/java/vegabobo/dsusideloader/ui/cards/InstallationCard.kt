package vegabobo.dsusideloader.ui.cards

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.ui.cards.content.NotInstallingCardContent
import vegabobo.dsusideloader.ui.cards.content.ProgressableCardContent
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.screen.home.InstallationCardState
import vegabobo.dsusideloader.ui.util.LauncherAcResult

@Composable
fun InstallationCard(
    uiState: InstallationCardState,
    modifier: Modifier = Modifier,
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    onClickRetryInstallation: () -> Unit,
    onClickUnmountSdCardAndRetry: () -> Unit,
    onClickSetSeLinuxPermissive: () -> Unit,
    onClickCancelInstallation: () -> Unit,
    onClickDiscardInstalledGsiAndInstall: () -> Unit,
    onClickDiscardDsu: () -> Unit,
    onClickRebootToDynOS: () -> Unit,
    onSelectFileSuccess: (Uri) -> Unit,
    onClickViewLogs: () -> Unit,
) {

    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "*/*"
    val mimetypes = arrayOf(
        "application/gzip",
        "application/x-gzip",
        "application/x-xz",
        "application/zip",
        "application/octet-stream"
    )
    chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    chooseFile = Intent.createChooser(chooseFile, "")

    val launcherSelectFile = LauncherAcResult {
        onSelectFileSuccess(it)
    }

    val textFieldInteraction = remember { MutableInteractionSource() }

    if (textFieldInteraction.collectIsPressedAsState().value)
        launcherSelectFile.launch(chooseFile)

    CardBox(
        cardTitle = stringResource(R.string.installation),
        addToggle = false,
        modifier = modifier
    ) {
        when (uiState.installationStep) {
            InstallationStep.NOT_INSTALLING ->
                NotInstallingCardContent(
                    textFieldInteraction = textFieldInteraction,
                    uiState = uiState,
                    onClickClear = onClickClear,
                    onClickInstall = onClickInstall
                )
            InstallationStep.DSU_ALREADY_INSTALLED ->
                ProgressableCardContent(
                    text = stringResource(R.string.dsu_already_installed),
                    textFirstButton = stringResource(id = R.string.reboot_dsu),
                    onClickFirstButton = onClickRebootToDynOS,
                    textSecondButton = stringResource(id = R.string.discard),
                    onClickSecondButton = onClickDiscardDsu
                )
            InstallationStep.PROCESSING ->
                ProgressableCardContent(
                    text = stringResource(R.string.processing),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.COPYING_FILE ->
                ProgressableCardContent(
                    text = stringResource(R.string.copying_file),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.DECOMPRESSING_XZ ->
                ProgressableCardContent(
                    text = stringResource(R.string.extracting_xz),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.COMPRESSING_TO_GZ ->
                ProgressableCardContent(
                    text = stringResource(R.string.compressing_img_to_gzip),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.DECOMPRESSING_GZIP ->
                ProgressableCardContent(
                    text = stringResource(R.string.extracting_file),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.EXTRACTING_FILE ->
                ProgressableCardContent(
                    text = stringResource(R.string.extracting_file),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.DISCARD_CURRENT_GSI -> {
                ProgressableCardContent(
                    text = stringResource(R.string.discard_gsi),
                    textFirstButton = stringResource(id = R.string.discard_dsu),
                    onClickFirstButton = onClickDiscardInstalledGsiAndInstall,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    progress = uiState.installationProgress
                )
            }
            InstallationStep.WAITING_USER_CONFIRMATION -> {
                ProgressableCardContent(
                    text = stringResource(R.string.installation_prompt),
                    textFirstButton = stringResource(id = R.string.try_again),
                    onClickFirstButton = onClickRetryInstallation,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            }
            InstallationStep.INSTALLING -> {
                ProgressableCardContent(
                    text = stringResource(R.string.installing, uiState.workingPartition),
                    textFirstButton = stringResource(id = R.string.cancel),
                    onClickFirstButton = onClickCancelInstallation,
                    textSecondButton = stringResource(id = R.string.view_logs),
                    onClickSecondButton = onClickViewLogs,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            }
            InstallationStep.INSTALLING_ROOTED -> {
                ProgressableCardContent(
                    text = stringResource(R.string.installing, uiState.workingPartition),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            }
            InstallationStep.CREATING_PARTITION ->
                ProgressableCardContent(
                    text = stringResource(R.string.creating_partition, uiState.workingPartition),
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation,
                    showProgressBar = true,
                    progress = uiState.installationProgress
                )
            InstallationStep.ERROR ->
                ProgressableCardContent(
                    text = stringResource(R.string.unknown_error, uiState.errorContent),
                    textFirstButton = stringResource(id = R.string.view_logs),
                    onClickFirstButton = onClickViewLogs,
                    textSecondButton = stringResource(id = R.string.mreturn),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.ERROR_CANCELED ->
                ProgressableCardContent(
                    text = stringResource(R.string.installation_canceled),
                    textFirstButton = stringResource(id = R.string.view_logs),
                    onClickFirstButton = onClickViewLogs,
                    textSecondButton = stringResource(id = R.string.mreturn),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.ERROR_REQUIRES_DISCARD_DSU ->
                ProgressableCardContent(
                    text = stringResource(R.string.discard_gsi),
                    textFirstButton = stringResource(id = R.string.discard),
                    onClickFirstButton = onClickDiscardInstalledGsiAndInstall,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            InstallationStep.ERROR_CREATE_PARTITION ->
                ProgressableCardContent(
                    text = stringResource(R.string.failed_create_partition),
                    textSecondButton = stringResource(id = R.string.mreturn),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.ERROR_EXTERNAL_SDCARD_ALLOC ->
                ProgressableCardContent(
                    text = stringResource(
                        R.string.allocation_error_description,
                        uiState.errorContent
                    ),
                    textFirstButton = stringResource(id = R.string.allocation_error_action),
                    onClickFirstButton = onClickUnmountSdCardAndRetry,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            InstallationStep.ERROR_NO_AVAIL_STORAGE ->
                ProgressableCardContent(
                    text = stringResource(R.string.storage_error_description, uiState.errorContent),
                    textFirstButton = stringResource(id = R.string.try_again),
                    onClickFirstButton = onClickRetryInstallation,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            InstallationStep.ERROR_F2FS_WRONG_PATH ->
                ProgressableCardContent(
                    text = stringResource(
                        R.string.filesystem_error_description,
                        uiState.errorContent
                    ),
                    textFirstButton = stringResource(id = R.string.view_logs),
                    onClickFirstButton = onClickViewLogs,
                    textSecondButton = stringResource(id = R.string.clear),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.ERROR_EXTENTS ->
                ProgressableCardContent(
                    text = stringResource(R.string.extents_error_description, uiState.errorContent),
                    textFirstButton = stringResource(id = R.string.view_logs),
                    onClickFirstButton = onClickViewLogs,
                    textSecondButton = stringResource(id = R.string.got_it),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.ERROR_SELINUX_A10 ->
                ProgressableCardContent(
                    text = stringResource(R.string.selinux_error_description, uiState.errorContent),
                    textFirstButton = stringResource(id = R.string.selinux_error_action),
                    onClickFirstButton = onClickSetSeLinuxPermissive,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            InstallationStep.ERROR_SELINUX_A10_ROOTLESS ->
                ProgressableCardContent(
                    text = stringResource(R.string.selinux_error_description, uiState.errorContent),
                    textFirstButton = stringResource(id = R.string.view_logs),
                    onClickFirstButton = onClickViewLogs,
                    textSecondButton = stringResource(id = R.string.cancel),
                    onClickSecondButton = onClickCancelInstallation
                )
            InstallationStep.INSTALL_SUCCESS ->
                ProgressableCardContent(
                    text = stringResource(R.string.done_text),
                    textSecondButton = stringResource(id = R.string.mreturn),
                    onClickSecondButton = onClickClear
                )
            InstallationStep.INSTALL_SUCCESS_REBOOT_DYN_OS ->
                ProgressableCardContent(
                    text = stringResource(R.string.installation_finished),
                    textFirstButton = stringResource(id = R.string.reboot_dsu),
                    onClickFirstButton = onClickRebootToDynOS,
                    textSecondButton = stringResource(id = R.string.discard),
                    onClickSecondButton = onClickDiscardDsu
                )
        }
    }
}
