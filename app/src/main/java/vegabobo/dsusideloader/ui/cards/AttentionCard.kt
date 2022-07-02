package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.ActionButton
import vegabobo.dsusideloader.ui.components.CardWithText

@Composable
fun AttentionCard(
    cardTitle: String = stringResource(id = R.string.storage),
    text: String = stringResource(id = R.string.storage_info),
    onClick: () -> Unit
) {
    CardWithText(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = cardTitle,
        text = text
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ActionButton(text = cardTitle, onClick = onClick)
        }
    }
}