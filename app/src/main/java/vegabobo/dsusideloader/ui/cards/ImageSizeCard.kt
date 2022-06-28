package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ImageSizeCard
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.UserdataCard
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun ImageSizeCard(
    cardTitle: String = stringResource(id = R.string.image_size),
    textFieldTitle: String = stringResource(id = R.string.image_size_custom),
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