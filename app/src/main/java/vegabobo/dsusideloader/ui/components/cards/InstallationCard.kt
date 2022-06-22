package vegabobo.dsusideloader.ui.components.cards

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun InstallationCard(
    cardTitle: String = stringResource(R.string.installation),
    btnClearTitle: String = stringResource(R.string.clear),
    btnInstallTitle: String = stringResource(R.string.install),
    onClickInstall: () -> Unit,
    onClickClear: () -> Unit,
    onClickTextField: (String) -> Unit,
    isError: Boolean = false,
    isEnabled: Boolean = true
) {
    CardBox(cardTitle = cardTitle, addToggle = false) {
        FileSelectionBox(
            onClickTextField = onClickTextField,
            enabled = isEnabled,
            isError = isError,
            value = stringResource(id = R.string.select_file),
            title = stringResource(id = R.string.select_gsi_info)
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row {
            Spacer(modifier = Modifier.weight(1F))
            Button(
                onClick = onClickInstall,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(text = btnClearTitle, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Button(onClick = onClickClear) {
                Text(text = btnInstallTitle)
            }
        }
    }
}