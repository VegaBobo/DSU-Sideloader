package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.Dialog

@Composable
fun DiscardDSUDialog(
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit
) {
    Dialog(
        title = stringResource(id = R.string.discard),
        text = stringResource(id = R.string.dsu_already_installed_warning),
        confirmText = stringResource(id = R.string.yes),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = onClickConfirm,
        onClickCancel = onClickCancel
    )
}