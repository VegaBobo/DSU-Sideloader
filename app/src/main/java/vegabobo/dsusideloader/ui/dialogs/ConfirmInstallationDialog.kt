package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.dsuhelper.GSI
import vegabobo.dsusideloader.ui.Dialog

@Composable
fun ConfirmInstallationDialog(
    title: String = stringResource(id = R.string.info),
    text: String = "",
    GSI: GSI,
    confirmText: String = stringResource(id = R.string.proceed),
    cancelText: String = stringResource(id = R.string.cancel),
    automaticSizeText: String = stringResource(id = R.string.auto),
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {

    val fileSize =
        if (GSI.fileSize == -1L)
            automaticSizeText
        else GSI.fileSize

    Dialog(
        title = title,
        text =
        text.ifEmpty {
            stringResource(
                id = R.string.installation_details,
                GSI.name!!,
                GSI.userdataSize,
                fileSize
            )
        },
        confirmText = confirmText,
        cancelText = cancelText,
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() })
}