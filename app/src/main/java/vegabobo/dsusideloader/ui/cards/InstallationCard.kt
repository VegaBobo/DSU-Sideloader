package vegabobo.dsusideloader.ui.cards

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox
import vegabobo.dsusideloader.ui.screen.home.InstallationCardState
import vegabobo.dsusideloader.ui.util.InstallationCardInstalling
import vegabobo.dsusideloader.ui.util.LauncherAcResult

@Composable
fun InstallationCard(
    isInstalling: Boolean,
    uiState: InstallationCardState,
    modifier: Modifier = Modifier,
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    onClickRetryInstallation: () -> Unit,
    onClickUnmountSdCardAndRetry: () -> Unit,
    onClickSetSeLinuxPermissive: () -> Unit,
    onClickCancelInstallation: () -> Unit,
    onClickDiscardInstalledGsi: () -> Unit,
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
        if (isInstalling) {
            InstallationCardInstalling(
                isInstalling = isInstalling,
                uiState = uiState,
                onClickClear = onClickClear,
                onClickRetryInstallation = onClickRetryInstallation,
                onClickUnmountSdCardAndRetry = onClickUnmountSdCardAndRetry,
                onClickSetSeLinuxPermissive = onClickSetSeLinuxPermissive,
                onClickCancelInstallation = onClickCancelInstallation,
                onClickDiscardInstalledGsi = onClickDiscardInstalledGsi,
                onClickRebootToDynOS = onClickRebootToDynOS,
                onClickViewLogs = onClickViewLogs
            )
        } else {
            FileSelectionBox(
                textFieldInteraction = textFieldInteraction,
                isEnabled = uiState.isTextFieldEnabled,
                isError = uiState.isError,
                isReadOnly = true,
                textFieldValue = uiState.content,
                textFieldTitle = stringResource(id = R.string.select_gsi_info)
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(visible = uiState.isError) {
                    Text(
                        text = stringResource(id = R.string.selected_file_not_supported),
                        modifier = Modifier.padding(start = 2.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                if (uiState.isInstallable) {
                    ActionButton(
                        text = stringResource(R.string.clear),
                        onClick = onClickClear,
                        colorText = MaterialTheme.colorScheme.primary,
                        colorButton = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(end = 6.dp))
                }
                ActionButton(
                    text = stringResource(R.string.install),
                    onClick = onClickInstall,
                    isEnabled = uiState.isInstallable
                )
            }
        }
    }
}
