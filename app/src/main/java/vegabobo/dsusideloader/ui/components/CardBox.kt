package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardBox(
    modifier: Modifier = Modifier,
    cardTitle: String = "",
    addToggle: Boolean = false,
    isToggleChecked: Boolean = false,
    isToggleEnabled: Boolean = true,
    addPadding: Boolean = true,
    cardColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    onCheckedChange: ((Boolean) -> Unit) = {},
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(10.dp),
    content: @Composable (ColumnScope) -> Unit,
) {
    Box(
        modifier = if (addPadding) {
            Modifier
                .clip(roundedCornerShape)
                .background(cardColor)
                .padding(all = 10.dp)
                .padding(end = 4.dp, start = 4.dp)
                .fillMaxWidth()
        } else {
            Modifier
                .clip(roundedCornerShape)
                .background(cardColor)
                .fillMaxWidth()
        },
    ) {
        Column(modifier = modifier) {
            if (cardTitle.isNotEmpty()) {
                if (addToggle) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        CardTitle(modifier.weight(1F), cardTitle = cardTitle)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Switch(
                            checked = isToggleChecked,
                            onCheckedChange = onCheckedChange,
                            enabled = isToggleEnabled,
                        )
                    }
                } else {
                    CardTitle(
                        cardTitle = cardTitle,
                        modifier = Modifier.padding(top = 9.5.dp, bottom = 9.5.dp),
                    )
                }
            }
            content(this)
        }
    }
}
