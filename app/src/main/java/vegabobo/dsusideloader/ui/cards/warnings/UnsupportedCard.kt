package vegabobo.dsusideloader.ui.cards.warnings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.ErrorButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton

@Composable
fun UnsupportedCard(
    onClickClose: () -> Unit = {},
    onClickContinueAnyway: () -> Unit,
) {
    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardColor = MaterialTheme.colorScheme.errorContainer,
        cardTitle = stringResource(id = R.string.unsupported),
        text = stringResource(id = R.string.device_unsupported_description),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Row {
                SecondaryButton(
                    text = stringResource(id = R.string.continue_anyway),
                    onClick = onClickContinueAnyway,
                )
                Spacer(modifier = Modifier.padding(end = 8.dp))
                ErrorButton(
                    onClick = onClickClose,
                    text = stringResource(id = R.string.close),
                )
            }
        }
    }
}
