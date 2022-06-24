package vegabobo.dsusideloader.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun ImageSizeCard(
    cardTitle: String = stringResource(id = R.string.image_size),
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
            Column {
                FileSelectionBox(
                    enabled = true,
                    isError = isError,
                    value = "",
                    title = stringResource(id = R.string.image_size_custom)
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(id = R.string.gsi_size_warning),
                    fontSize = 12.sp
                )
            }
        }
    }
}