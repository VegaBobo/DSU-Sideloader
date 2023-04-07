package vegabobo.dsusideloader.ui.cards

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton

@Composable
fun CopyableTextCard(
    text: String,
    showToast: Boolean = true,
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val copiedText = stringResource(id = R.string.copied)

    SimpleCard(
        text = text,
        content = {
            Row {
                Spacer(modifier = Modifier.weight(1F))
                PrimaryButton(
                    text = stringResource(id = R.string.copy_text),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(text))
                        if (showToast) {
                            Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        },
    )
}
