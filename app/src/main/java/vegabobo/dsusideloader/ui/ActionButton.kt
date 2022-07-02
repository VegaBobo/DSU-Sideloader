package vegabobo.dsusideloader.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    colorButton: Color? = null,
    colorText: Color? = null,
    isEnabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick, enabled = isEnabled,
        colors =
        if (colorButton != null) ButtonDefaults.buttonColors(colorButton)
        else ButtonDefaults.buttonColors()
    ) {
        Text(
            text = text,
            color = colorText ?: MaterialTheme.colorScheme.surface
        )
    }
}