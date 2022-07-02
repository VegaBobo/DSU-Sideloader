package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.ActionButton
import vegabobo.dsusideloader.ui.components.CardWithText

@Composable
fun StorageWarningCard(
    cardTitle: String = stringResource(id = R.string.storage),
    text: String = stringResource(id = R.string.storage_warning),
    onClick: () -> Unit
) {
    CardWithText(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = cardTitle,
        text = text
    ){
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ActionButton(text = "Continue anyway", onClick = onClick)
        }
    }
}