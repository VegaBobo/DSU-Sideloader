package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogLikeBottomSheet(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String = "",
    text: String = "",
    confirmText: String = "",
    cancelText: String = "",
    hideKeyboard: Boolean = true,
    onClickConfirm: () -> Unit = {},
    onClickCancel: () -> Unit = {},
    onDismiss: () -> Unit = onClickCancel,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    if (hideKeyboard) {
        LocalSoftwareKeyboardController.current?.hide()
        LocalFocusManager.current.clearFocus()
    }

    CustomBottomSheet(
        modifier = modifier,
        title = title,
        icon = icon,
        onDismiss = onDismiss,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onBackground,
        )
        content()
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Spacer(modifier = Modifier.weight(1F))
            SecondaryButton(
                text = cancelText,
                onClick = { coroutineScope.launch { it() /* Hide Sheet before cancelling */; onClickCancel() } },
            )
            Spacer(modifier = Modifier.padding(4.dp))
            PrimaryButton(
                text = confirmText,
                onClick = { coroutineScope.launch { it() /* Hide Sheet before confirming */; onClickConfirm() } },
            )
        }
    }
}
