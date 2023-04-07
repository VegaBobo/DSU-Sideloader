package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox
import vegabobo.dsusideloader.ui.screen.home.ImageSizeCardState

@Composable
fun ImageSizeCard(
    isEnabled: Boolean,
    uiState: ImageSizeCardState,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        modifier = modifier,
        cardTitle = stringResource(id = R.string.image_size),
        addToggle = true,
        isToggleEnabled = !isEnabled,
        isToggleChecked = uiState.isSelected,
        onCheckedChange = onCheckedChange,
    ) {
        AnimatedVisibility(
            visible = uiState.isSelected,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                FileSelectionBox(
                    modifier = Modifier.padding(bottom = 4.dp),
                    isEnabled = !isEnabled,
                    isError = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textFieldValue = uiState.text,
                    textFieldTitle = stringResource(id = R.string.image_size_info),
                    onValueChange = onValueChange,
                )
                AnimatedVisibility(visible = uiState.text.isEmpty()) {
                    Text(
                        modifier = Modifier.padding(start = 1.dp),
                        text = stringResource(id = R.string.not_recommended_option),
                        color = MaterialTheme.colorScheme.error,
                        lineHeight = 14.sp,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}
