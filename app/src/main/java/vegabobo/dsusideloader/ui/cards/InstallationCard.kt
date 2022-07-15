package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun InstallationCard(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isInstallable: Boolean = false,
    isInstalling: Boolean = false,
    textFieldText: String = "",
    installationText: String = "",
    installationProgressBar: Float = 0.0f,
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    onClickTextField: () -> Unit,
) {

    val textFieldInteraction = remember { MutableInteractionSource() }

    if (textFieldInteraction.collectIsPressedAsState().value)
        onClickTextField()

    CardBox(cardTitle = stringResource(R.string.installation), addToggle = false, modifier = modifier) {
        Spacer(modifier = Modifier.padding(top = 2.dp))
        if (isInstalling) {
            Text(text = installationText)
            Spacer(modifier = Modifier.padding(top = 10.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = installationProgressBar
            )
        } else {
            FileSelectionBox(
                textFieldInteraction = textFieldInteraction,
                isEnabled = isEnabled,
                isError = isError,
                isReadOnly = true,
                textFieldValue = textFieldText,
                textFieldTitle = stringResource(id = R.string.select_gsi_info)
            )
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row {
            Spacer(modifier = Modifier.weight(1F))
            if (isInstallable && !isInstalling) {
                ActionButton(
                    text = stringResource(R.string.clear),
                    onClick = onClickClear,
                    colorText = MaterialTheme.colorScheme.primary,
                    colorButton = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.padding(end = 6.dp))
            }
            ActionButton(
                text = if (isInstalling) stringResource(id = R.string.cancel) else stringResource(R.string.install),
                onClick = onClickInstall,
                isEnabled = isInstallable
            )
        }
    }
}