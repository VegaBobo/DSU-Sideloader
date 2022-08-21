package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.screen.home.UserDataCardState
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.FileSelectionBox

@Composable
fun UserdataCard(
    isEnabled: Boolean,
    uiState: UserDataCardState,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    CardBox(
        modifier = modifier,
        cardTitle = stringResource(id = R.string.userdata_size_ct),
        addToggle = true,
        isToggleEnabled = !isEnabled,
        isToggleChecked = uiState.isSelected,
        onCheckedChange = onCheckedChange
    ) {
        AnimatedVisibility(visible = uiState.isSelected) {
            Column {
                FileSelectionBox(
                    modifier = Modifier.padding(bottom = 4.dp),
                    isEnabled = !isEnabled,
                    isError = uiState.isError,
                    isNumberOnly = true,
                    textFieldValue = uiState.content,
                    textFieldTitle = stringResource(id = R.string.userdata_size_n),
                    onValueChange = onValueChange
                )
                AnimatedVisibility(visible = uiState.isError) {
                    Text(
                        modifier = Modifier.padding(start = 1.dp),
                        text = stringResource(
                            id = R.string.allowed_userdata_allocation,
                            uiState.maximumAllowed
                        ),
                        color = MaterialTheme.colorScheme.error,
                        lineHeight = 14.sp,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}