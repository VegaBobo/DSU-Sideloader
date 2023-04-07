package vegabobo.dsusideloader.ui.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrimaryButton(
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
    )
}
