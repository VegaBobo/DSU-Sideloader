package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
    context: @Composable () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        icon = { Icon(icon, "icon") },
        onDismissRequest = onClickCancel,
        confirmButton = {
            if (confirmText.isNotEmpty())
                TextButton(onClick = onClickConfirm) {
                    Text(text = confirmText)
                }
        },
        dismissButton = {
            if (cancelText.isNotEmpty())
                TextButton(onClick = onClickCancel) {
                    Text(text = cancelText)
                }
        },
        text = {
            Column {
                if (title.isNotEmpty())
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )
                Spacer(modifier = Modifier.padding(8.dp))
                if (text.isNotEmpty())
                    Text(
                        text = text
                    )
                context()
            }
        })
}