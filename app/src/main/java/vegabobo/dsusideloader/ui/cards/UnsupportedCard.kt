package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.CardWithText

@Composable
fun UnsupportedCard(
    onClickButton: () -> Unit = {}
) {
    CardWithText(
        modifier = Modifier.fillMaxWidth(),
        cardColor = MaterialTheme.colorScheme.errorContainer,
        cardTitle = stringResource(id = R.string.unsupported),
        text = stringResource(id = R.string.device_unsupported)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ActionButton(
                onClick = onClickButton,
                text = stringResource(id = R.string.close),
                colorButton = MaterialTheme.colorScheme.error
            )
        }
    }
}