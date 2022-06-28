package vegabobo.dsusideloader.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    altColor: Boolean = false
) {
    FilledTonalButton(
        onClick = onClick, enabled = isEnabled,
        colors =
        if (altColor) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceVariant)
        else ButtonDefaults.buttonColors()
    ) {
        Text(
            text = text,
            color = if (altColor) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    }
}