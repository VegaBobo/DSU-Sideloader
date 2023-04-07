package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferenceItem(
    title: String,
    description: String = "",
    icon: ImageVector? = null,
    onClick: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
    showToggle: Boolean = false,
    isEnabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(isChecked) }, enabled = isEnabled)
            .padding(
                start = 17.dp,
                end = 17.dp,
                bottom = 12.dp,
                top = 12.dp,
            ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 22.dp),
            )
        }
        Row(modifier = Modifier.weight(0.5F)) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.headlineMedium,
                )
                if (description.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.60F),
                    )
                }
            }
        }
        if (showToggle) {
            Switch(
                checked = isChecked,
                enabled = isEnabled,
                onCheckedChange = { onClick(isChecked) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
