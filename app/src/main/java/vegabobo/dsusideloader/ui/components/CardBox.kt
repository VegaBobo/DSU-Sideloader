package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    cardColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    onCheckedChange: ((Boolean) -> Unit) = {},
    content: @Composable (ColumnScope) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(cardColor)
            .padding(all = 10.dp)
            .padding(end = 4.dp, start = 4.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = modifier) {
            if (cardTitle.isNotEmpty()) {
                if (addToggle)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CardTitle(cardTitle = cardTitle)
                        Spacer(modifier = Modifier.weight(1F))
                        Switch(
                            checked = isToggleChecked,
                            onCheckedChange = onCheckedChange,
                            enabled = isToggleEnabled
                        )
                    }
                else
                    CardTitle(
                        cardTitle = cardTitle,
                        modifier = Modifier.padding(top = 9.5.dp, bottom = 9.5.dp)
                    )
            }
            content(this)
        }
    }
}