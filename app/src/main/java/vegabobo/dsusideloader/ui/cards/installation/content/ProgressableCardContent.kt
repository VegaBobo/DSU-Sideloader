package vegabobo.dsusideloader.ui.cards.installation.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton

@Composable
fun ProgressableCardContent(
    text: String,
    showProgressBar: Boolean = false,
    isIndeterminate: Boolean = false,
    progress: Float = 0F,
    textFirstButton: String = "",
    textSecondButton: String = "",
    onClickFirstButton: (() -> Unit)? = null,
    onClickSecondButton: (() -> Unit)? = null,
) {
    Text(text = text)
    AnimatedVisibility(visible = showProgressBar) {
        val progressBarModifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 5.dp)
        if (isIndeterminate) {
            LinearProgressIndicator(modifier = progressBarModifier)
        } else {
            LinearProgressIndicator(
                modifier = progressBarModifier,
                progress = progress,
            )
        }
    }
    Spacer(modifier = Modifier.padding(top = 4.dp))
    Row {
        Spacer(modifier = Modifier.weight(1F))
        if (onClickSecondButton != null) {
            SecondaryButton(
                text = textSecondButton,
                onClick = onClickSecondButton,
            )
        }
        if (onClickFirstButton != null && onClickSecondButton != null) {
            Spacer(modifier = Modifier.padding(end = 6.dp))
        }
        if (onClickFirstButton != null) {
            PrimaryButton(
                text = textFirstButton,
                onClick = onClickFirstButton,
            )
        }
    }
}
