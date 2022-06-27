package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun CardWithTextField(
    cardTitle: String,
    textFieldTitle: String,
    addToggle: Boolean = true,
    isToggleEnabled: Boolean = false,
    isError: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        cardTitle = cardTitle,
        addToggle = addToggle,
        isToggleEnabled = isToggleEnabled,
        onCheckedChange = onCheckedChange
    ) {
        AnimatedVisibility(visible = isToggleEnabled) {
            FileSelectionBox(
                enabled = true,
                isError = isError,
                numberOnly = true,
                value = value,
                title = textFieldTitle,
                onValueChange = onValueChange
            )
        }
    }
}