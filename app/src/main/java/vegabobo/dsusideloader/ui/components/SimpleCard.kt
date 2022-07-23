package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SimpleCard(
    modifier: Modifier = Modifier,
    cardTitle: String = "",
    text: String = "",
    addToggle: Boolean = false,
    isToggleEnabled: Boolean = false,
    cardColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    justifyText: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    CardBox(
        modifier = modifier,
        cardTitle = cardTitle,
        addToggle = addToggle,
        isToggleChecked = isToggleEnabled,
        cardColor = cardColor,
    ) {
        Text(
            text = text,
            textAlign = if (justifyText) TextAlign.Justify else TextAlign.Start
        )
        content()
    }
}