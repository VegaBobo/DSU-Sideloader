package vegabobo.dsusideloader.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    colorButton: Color? = null,
    colorText: Color? = null,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick, enabled = isEnabled,
        colors =
        if (colorButton != null) ButtonDefaults.buttonColors(colorButton)
        else ButtonDefaults.buttonColors()
    ) {
        Text(
            text = text,
            color = colorText ?: MaterialTheme.colorScheme.surface
        )
        content()
    }
}