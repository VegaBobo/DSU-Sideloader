package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSelectionBox(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    isEnabled: Boolean,
    isError: Boolean,
    textFieldTitle: String,
    textFieldValue: String,
    textFieldInteraction: MutableInteractionSource = MutableInteractionSource(),
    onValueChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue,
        placeholder = { Text(text = "") },
        onValueChange = onValueChange,
        enabled = isEnabled,
        isError = isError,
        singleLine = true,
        readOnly = isReadOnly,
        keyboardOptions = keyboardOptions,
        interactionSource = textFieldInteraction,
        label = { Text(text = textFieldTitle) },
    )
}
