package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun UserdataCard(
    addToggle: Boolean = true,
    isToggleEnabled: Boolean = false,
    isError: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        cardTitle = stringResource(id = R.string.userdata_size_ct),
        addToggle = addToggle,
        isToggleEnabled = isToggleEnabled,
        onCheckedChange = onCheckedChange
    ) {
        AnimatedVisibility(visible = isToggleEnabled) {
            FileSelectionBox(
                isEnabled = true,
                isError = isError,
                isNumberOnly = true,
                textFieldValue = value,
                textFieldTitle = stringResource(id = R.string.userdata_size_n),
                onValueChange = onValueChange
            )
        }
    }
}