package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun FileSelectionBox(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    isNumberOnly: Boolean = false,
    isEnabled: Boolean,
    isError: Boolean,
    textFieldTitle: String,
    textFieldValue: String,
    textFieldInteraction: MutableInteractionSource? = MutableInteractionSource(),
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = textFieldValue,
        placeholder = { Text(text = "") },
        onValueChange = onValueChange,
        enabled = isEnabled,
        isError = isError,
        singleLine = true,
        readOnly = isReadOnly,
        keyboardOptions = if (isNumberOnly) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions(),
        interactionSource = textFieldInteraction!!,
        label = {
            Text(text = textFieldTitle)
        })
}