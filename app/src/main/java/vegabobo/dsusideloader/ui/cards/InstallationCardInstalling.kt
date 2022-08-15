package vegabobo.dsusideloader.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preparation.InstallationSteps
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.screen.home.InstallationCardState

@Composable
fun InstallationCardInstalling(
    isInstalling: Boolean,
    uiState: InstallationCardState,
    onClickClear: () -> Unit,
    onClickRetryInstallation: () -> Unit,
    onClickUnmountSdCardAndRetry: () -> Unit,
    onClickSetSeLinuxPermissive: () -> Unit,
    onClickCancelInstallation: () -> Unit,
    onClickDiscardInstalledGsi: () -> Unit,
    onClickRebootToDynOS: () -> Unit,
    onClickViewLogs: () -> Unit
) {
    val text = mutableStateOf("")
    var onClickBtn1: (() -> Unit)? = null
    var onClickBtn2: (() -> Unit)? = null
    var textBtn1 = ""
    var textBtn2 = ""

    if (!isInstalling) {
        textBtn1 = stringResource(R.string.cancel)
        onClickBtn1 = onClickCancelInstallation
        if (uiState.isInstallable) {
            textBtn2 = stringResource(R.string.clear)
            onClickBtn2 = onClickClear
        }
    } else {
        when (uiState.installationStep) {
            InstallationSteps.COPYING_FILE -> {
                text.value = stringResource(R.string.gz_copy)
                onClickBtn2 = onClickCancelInstallation
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.DECOMPRESSING_XZ -> {
                text.value = stringResource(R.string.extracting_gzip)
                onClickBtn2 = onClickCancelInstallation
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.COMPRESSING_TO_GZ -> {
                text.value = stringResource(R.string.compressing_img_to_gzip)
                onClickBtn2 = onClickCancelInstallation
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.DECOMPRESSING_GZIP -> {
                text.value = stringResource(R.string.extracting_gzip)
                onClickBtn2 = onClickCancelInstallation
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.DISCARD_CURRENT_GSI -> {
                text.value = stringResource(R.string.discard_gsi)
                onClickBtn1 = onClickDiscardInstalledGsi
                onClickBtn2 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.discard_dsu)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.WAITING_USER_CONFIRMATION -> {
                text.value = stringResource(R.string.installation_prompt)
                onClickBtn1 = onClickRetryInstallation
                onClickBtn2 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.try_again)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.ERROR_NO_AVAIL_STORAGE -> {
                text.value = stringResource(R.string.storage_error_description)
                onClickBtn1 = onClickRetryInstallation
                onClickBtn2 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.try_again)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.ERROR_EXTERNAL_SDCARD_ALLOC -> {
                text.value = stringResource(R.string.allocation_error_description)
                onClickBtn1 = onClickUnmountSdCardAndRetry
                onClickBtn2 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.allocation_error_action)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.ERROR_F2FS_WRONG_PATH -> {
                text.value = stringResource(R.string.filesystem_error_description)
                onClickBtn1 = onClickClear
                onClickBtn2 = onClickViewLogs
                textBtn1 = stringResource(id = R.string.got_it)
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.ERROR_SELINUX_A10 -> {
                text.value = stringResource(R.string.selinux_error_description)
                onClickBtn1 = onClickSetSeLinuxPermissive
                onClickBtn2 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.selinux_error_action)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.ERROR_SELINUX_A10_ROOTLESS -> {
                text.value = stringResource(R.string.selinux_error_description)
                onClickBtn1 = onClickCancelInstallation
                onClickBtn2 = onClickViewLogs
                textBtn1 = stringResource(id = R.string.cancel)
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.ERROR_EXTENTS -> {
                text.value = stringResource(R.string.extents_error_description)
                onClickBtn1 = onClickClear
                onClickBtn2 = onClickViewLogs
                textBtn1 = stringResource(id = R.string.got_it)
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.CANCELED -> {
                text.value = stringResource(R.string.installation_canceled)
                onClickBtn1 = onClickClear
                onClickBtn2 = onClickViewLogs
                textBtn1 = stringResource(id = R.string.mreturn)
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.ERROR_GENERIC_ERROR -> {
                text.value = stringResource(R.string.unknown_error)
                onClickBtn1 = onClickClear
                onClickBtn2 = onClickViewLogs
                textBtn1 = stringResource(id = R.string.mreturn)
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.INSTALLING -> {
                text.value = stringResource(R.string.installing, uiState.workingPartition)
                onClickBtn1 = onClickCancelInstallation
                textBtn1 = stringResource(id = R.string.cancel)
                onClickBtn2 = onClickViewLogs
                textBtn2 = stringResource(id = R.string.view_logs)
            }
            InstallationSteps.READY_TO_INSTALL -> {
                text.value = stringResource(R.string.process_finished)
                onClickBtn1 = onClickRebootToDynOS
                onClickBtn2 = onClickDiscardInstalledGsi
                textBtn1 = stringResource(id = R.string.discard_dsu)
                textBtn2 = stringResource(id = R.string.cancel)
            }
            InstallationSteps.DONE -> {
                text.value = stringResource(R.string.done_text)
                onClickBtn1 = onClickClear
                onClickBtn2 = null
                textBtn1 = stringResource(id = R.string.mreturn)
                textBtn2 = ""
            }
            InstallationSteps.NONE -> {}
            InstallationSteps.EXTRACTING_FILE -> TODO()
        }
    }

    Text(text = text.value)
    AnimatedVisibility(visible = uiState.isShowingProgressBar) {
        if (!uiState.isIndeterminate)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 5.dp),
                progress = uiState.installationProgress
            )
        else
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 5.dp),
            )
    }

    Spacer(modifier = Modifier.padding(top = 4.dp))

    Row {
        Spacer(modifier = Modifier.weight(1F))
        if (onClickBtn2 != null)
            ActionButton(
                modifier = Modifier.padding(end = 6.dp),
                text = textBtn2,
                onClick = { onClickBtn2() },
                isEnabled = uiState.isInstallable,
                colorText = MaterialTheme.colorScheme.primary,
                colorButton = MaterialTheme.colorScheme.surfaceVariant
            )

        if (onClickBtn1 != null)
            ActionButton(
                text = textBtn1,
                onClick = { onClickBtn1() }
            )
    }
}