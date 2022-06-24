package vegabobo.dsusideloader.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun UserdataCard(
    cardTitle: String = stringResource(id = R.string.userdata_size_ct),
    addToggle: Boolean = true,
    isToggleEnabled: Boolean = false,
    isError: Boolean,
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
                value = stringResource(id = R.string.userdata_size_gb),
                title = stringResource(id = R.string.userdata_size_n)
            )
        }
    }
}