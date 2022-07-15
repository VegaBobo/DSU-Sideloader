package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
    modifier: Modifier = Modifier,
    addToggle: Boolean = true,
    isToggleChecked: Boolean = false,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        modifier = modifier,
        cardTitle = stringResource(id = R.string.image_size),
        addToggle = addToggle,
        isToggleEnabled = isEnabled,
        isToggleChecked = isToggleChecked,
        onCheckedChange = onCheckedChange
    ) {
        AnimatedVisibility(visible = isToggleChecked) {
            Column {
                FileSelectionBox(
                    modifier = Modifier.padding(bottom = 4.dp),
                    isEnabled = isEnabled,
                    isError = isError,
                    isNumberOnly = true,
                    textFieldValue = textFieldValue,
                    textFieldTitle = stringResource(id = R.string.image_size_custom),
                    onValueChange = onValueChange
                )
                AnimatedVisibility(visible = textFieldValue.isEmpty()) {
                    Text(
                        modifier = Modifier.padding(start = 1.dp),
                        text = stringResource(id = R.string.option_strongly_inadvisable),
                        color = MaterialTheme.colorScheme.error,
                        lineHeight = 14.sp,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}