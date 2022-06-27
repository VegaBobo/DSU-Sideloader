package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FileSelectionBox(
    enabled: Boolean,
    textFieldInteraction: MutableInteractionSource? = MutableInteractionSource(),
    isError: Boolean,
    value: String,
    placeholder: String = "",
    title: String,
    readOnly: Boolean = false,
    numberOnly: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        placeholder = { Text(text = placeholder) },
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        readOnly = readOnly,
        keyboardOptions = if (numberOnly) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions(),
        interactionSource = textFieldInteraction!!,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        label = {
            Text(text = title)
        })
}