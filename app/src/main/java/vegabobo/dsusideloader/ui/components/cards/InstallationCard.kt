package vegabobo.dsusideloader.ui.components.cards

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.ActionButton
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun InstallationCard(
    cardTitle: String = stringResource(R.string.installation),
    btnClearTitle: String = stringResource(R.string.clear),
    btnInstallTitle: String = stringResource(R.string.install),
    textFieldText: String = "",
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    onClickTextField: () -> Unit,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isInstallable: Boolean = false
) {

    val textFieldInteraction = remember { MutableInteractionSource() }

    if (textFieldInteraction.collectIsPressedAsState().value)
        onClickTextField()

    CardBox(cardTitle = cardTitle, addToggle = false) {
        FileSelectionBox(
            textFieldInteraction = textFieldInteraction,
            enabled = isEnabled,
            isError = isError,
            readOnly = true,
            value = textFieldText,
            title = stringResource(id = R.string.select_gsi_info)
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row {
            Spacer(modifier = Modifier.weight(1F))
            if (isInstallable)
                ActionButton(text = btnClearTitle, onClick = onClickClear, altColor = true)
            Spacer(modifier = Modifier.padding(4.dp))
            ActionButton(
                text = btnInstallTitle,
                onClick = onClickInstall,
                isEnabled = isInstallable
            )
        }
    }
}