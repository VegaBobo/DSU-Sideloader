package vegabobo.dsusideloader.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Dialog(
    title: String = "",
    text: String = "",
    confirmText: String = "",
    cancelText: String = "",
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
    context: @Composable () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onClickCancel,
        confirmButton = {
            if (confirmText.isNotEmpty())
                ActionButton(text = confirmText, onClick = onClickConfirm)
        },
        dismissButton = {
            if (cancelText.isNotEmpty())
                ActionButton(text = cancelText, onClick = onClickCancel, altColor = true)
        },
        text = {
            Column {
                if (confirmText.isNotEmpty())
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                if (cancelText.isNotEmpty())
                    Text(
                        text = text
                    )
                context()
            }
        })
}