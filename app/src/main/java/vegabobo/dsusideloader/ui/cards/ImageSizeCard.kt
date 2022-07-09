package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun ImageSizeCard(
    addToggle: Boolean = true,
    isToggleEnabled: Boolean = false,
    isError: Boolean = false,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        cardTitle = stringResource(id = R.string.image_size),
        addToggle = addToggle,
        isToggleEnabled = isToggleEnabled,
        onCheckedChange = onCheckedChange
    ) {
        AnimatedVisibility(visible = isToggleEnabled) {
            FileSelectionBox(
                isEnabled = true,
                isError = isError,
                isNumberOnly = true,
                textFieldValue = textFieldValue,
                textFieldTitle = stringResource(id = R.string.image_size_custom),
                onValueChange = onValueChange
            )
        }
    }
}