package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.screen.home.ManageDsuCardState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SlotManagerCard(
    modifier: Modifier = Modifier,
    uiState: ManageDsuCardState,
    onClickSlot: (String) -> Unit,
    onClickRebootToSlot: () -> Unit,
    onClickDiscardSlot: () -> Unit,
    onClickBack: () -> Unit,
) {
    SimpleCard(
        cardTitle = stringResource(id = R.string.slot_manager),
    ) {
        if (uiState.isSlotSelected) {
            Text(text = "Selected slot is: ${uiState.selectedSlot}")
            TextButton(onClick = { onClickRebootToSlot() }) {
                Text(stringResource(R.string.reboot_to_slot))
            }
            TextButton(onClick = { onClickDiscardSlot() }) {
                Text(stringResource(R.string.discard_slot))
            }
            TextButton(onClick = { onClickBack() }) {
                Text(stringResource(R.string.back))
            }
        } else {
            FlowRow(
                modifier = modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                maxItemsInEachRow = 4,
            ) {
                uiState.slotsDetected.forEach { item ->
                    Column(
                        modifier = Modifier
                            .clickable { onClickSlot(item) }
                            .padding(4.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = item)
                        Text(text = "slot", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
