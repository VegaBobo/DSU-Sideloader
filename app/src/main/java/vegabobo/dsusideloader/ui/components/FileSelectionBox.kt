package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R

@Composable
fun FileSelectionBox(
    enabled: Boolean,
    onClickTextField: (String) -> Unit,
    isError: Boolean,
    value: String,
    title: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onClickTextField,
        enabled = enabled,
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        label = {
            Text(text = title)
        })
}