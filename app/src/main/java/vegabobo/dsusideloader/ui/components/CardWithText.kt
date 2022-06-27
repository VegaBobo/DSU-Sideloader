package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.components.CardBox

@Composable
fun CardWithText(
    cardTitle: String,
    text: String,
    addToggle: Boolean = false,
    isToggleEnabled: Boolean = false,
) {
    CardBox(
        cardTitle = cardTitle,
        addToggle = addToggle,
        isToggleEnabled = isToggleEnabled,
    ) {
        Text(text = text, modifier = Modifier.padding(top = 10.dp))
    }
}