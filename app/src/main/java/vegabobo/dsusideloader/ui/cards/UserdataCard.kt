package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun UserdataCard(
    cardTitle: String = stringResource(id = R.string.userdata_size_ct),
    textFieldTitle: String = stringResource(id = R.string.userdata_size_n),
    addToggle: Boolean = true,
    isToggleEnabled: Boolean = false,
    isError: Boolean = false,
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