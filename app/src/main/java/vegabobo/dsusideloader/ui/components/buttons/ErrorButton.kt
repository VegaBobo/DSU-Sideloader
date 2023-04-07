package vegabobo.dsusideloader.ui.components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
) {
    ActionButton(
        modifier = modifier,
        text = text,
        onClick = onClick,
        isEnabled = isEnabled,
        colorButton = MaterialTheme.colorScheme.error,
    )
}
