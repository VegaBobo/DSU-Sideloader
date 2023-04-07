package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String = "",
    text: String = "",
    confirmText: String = "",
    cancelText: String = "",
    onClickConfirm: () -> Unit = {},
    onClickCancel: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        icon = { Icon(icon, "icon") },
        onDismissRequest = onClickCancel,
        confirmButton = {
            if (confirmText.isNotEmpty()) {
                TextButton(onClick = onClickConfirm) {
                    Text(text = confirmText)
                }
            }
        },
        dismissButton = {
            if (cancelText.isNotEmpty()) {
                TextButton(onClick = onClickCancel) {
                    Text(text = cancelText)
                }
            }
        },
        text = {
            Column {
                if (title.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                    )
                }
                content()
            }
        },
    )
}
