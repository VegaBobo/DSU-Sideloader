package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R

@Composable
fun CopyTextButton(
    isCopied: Boolean = false,
    onClickCopy: () -> Unit
) {
    Row {
        Spacer(modifier = Modifier.weight(1F))
        ActionButton(
            text = if (isCopied) stringResource(id = R.string.copied) else stringResource(id = R.string.copy),
            onClick = { onClickCopy() },
            content = {
                AnimatedVisibility(visible = isCopied) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "Done Icon",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            })
    }
}