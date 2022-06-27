package vegabobo.dsusideloader.ui

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    altColor: Boolean = false
) {
    Button(
        onClick = onClick, enabled = isEnabled,
        colors =
        if (altColor) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
        else ButtonDefaults.buttonColors()
    ) {
        Text(
            text = text,
            color = if (altColor) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    }
}