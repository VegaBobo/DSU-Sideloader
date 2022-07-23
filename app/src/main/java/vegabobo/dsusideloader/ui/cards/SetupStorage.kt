package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.SimpleCard

@Composable
fun SetupStorage(
    onClick: () -> Unit
) {
    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = stringResource(id = R.string.storage),
        text = stringResource(id = R.string.storage_info)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ActionButton(text = stringResource(id = R.string.storage), onClick = onClick)
        }
    }
}