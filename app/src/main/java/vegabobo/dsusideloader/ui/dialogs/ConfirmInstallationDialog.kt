package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.ui.components.Dialog

@Composable
fun ConfirmInstallationDialog(
    dialogText: String = "",
    GSI: GSI,
    filename: String,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {

    val fileSize =
        if (GSI.fileSize == -1L)
            stringResource(id = R.string.auto)
        else GSI.fileSize

    Dialog(
        title = stringResource(id = R.string.info),
        text =
        dialogText.ifEmpty {
            stringResource(
                id = R.string.installation_details,
                filename,
                GSI.obtainUserdataInGb(),
                fileSize
            )
        },
        confirmText = stringResource(id = R.string.proceed),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() })
}