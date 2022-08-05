package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.Dialog

@Composable
fun ImageSizeWarningDialog(
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit
) {
    Dialog(
        title = stringResource(id = R.string.custom_image_size),
        text = stringResource(id = R.string.custom_image_size_warning),
        confirmText = stringResource(id = R.string.set_anyway),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = onClickConfirm,
        onClickCancel = onClickCancel
    )
}