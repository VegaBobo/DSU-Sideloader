package vegabobo.dsusideloader.ui.components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.roundToInt

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
) {
    fun addAlpha(c: Int, a: Float): Color =
        Color(
            android.graphics.Color.argb(
                (android.graphics.Color.alpha(c) * a).roundToInt(),
                c.red,
                c.green,
                c.blue,
            ),
        )

    val onSecondaryVariant = MaterialTheme.colorScheme.onSecondaryContainer.toArgb()
    val colorButton = addAlpha(onSecondaryVariant, 0.05F)
    val colorText = addAlpha(onSecondaryVariant, 0.75F)

    ActionButton(
        modifier = modifier,
        text = text,
        onClick = onClick,
        isEnabled = isEnabled,
        colorButton = colorButton,
        colorText = colorText,
    )
}
