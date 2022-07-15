package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    title: String = "",
    text: String = "",
    confirmText: String = "",
    cancelText: String = "",
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
    context: @Composable () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onClickCancel,
        confirmButton = {
            if (confirmText.isNotEmpty())
                ActionButton(text = confirmText, onClick = onClickConfirm)
        },
        dismissButton = {
            if (cancelText.isNotEmpty())
                ActionButton(
                    text = cancelText, onClick = onClickCancel,
                    colorText = MaterialTheme.colorScheme.primary,
                    colorButton = MaterialTheme.colorScheme.surfaceVariant
                )
        },
        text = {
            Column {
                if (title.isNotEmpty())
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.headlineSmall,
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