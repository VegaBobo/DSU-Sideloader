package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SimpleCard(
    modifier: Modifier = Modifier,
    cardTitle: String = "",
    text: String = "",
    addToggle: Boolean = false,
    isToggleEnabled: Boolean = false,
    cardColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    justifyText: Boolean = false,
    textScrollable: Boolean = false,
    addPadding: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    CardBox(
        modifier = modifier,
        cardTitle = cardTitle,
        addToggle = addToggle,
        isToggleChecked = isToggleEnabled,
        addPadding = addPadding,
        cardColor = cardColor,
    ) {
        val scroll = rememberScrollState(0)
        if (text.isNotEmpty()) {
            Text(
                text = text,
                textAlign = if (justifyText) TextAlign.Justify else TextAlign.Start,
                modifier = if (textScrollable) Modifier.verticalScroll(scroll) else Modifier,
            )
        }
        content()
    }
}
