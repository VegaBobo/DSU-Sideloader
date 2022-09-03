package vegabobo.dsusideloader.ui.cards.warnings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.ErrorButton

@Composable
fun UnsupportedCard(
    onClickButton: () -> Unit = {}
) {
    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardColor = MaterialTheme.colorScheme.errorContainer,
        cardTitle = stringResource(id = R.string.unsupported),
        text = stringResource(id = R.string.device_unsupported_description)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ErrorButton(
                onClick = onClickButton,
                text = stringResource(id = R.string.close)
            )
        }
    }
}