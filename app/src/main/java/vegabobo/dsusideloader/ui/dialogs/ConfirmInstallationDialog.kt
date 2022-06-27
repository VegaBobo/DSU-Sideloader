package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.dsuhelper.GsiDsuObject
import vegabobo.dsusideloader.ui.Dialog

@Composable
fun ConfirmInstallationDialog(
    title: String = stringResource(id = R.string.info),
    text: String = "",
    gsiDsuObject: GsiDsuObject,
    confirmText: String = stringResource(id = R.string.proceed),
    cancelText: String = stringResource(id = R.string.cancel),
    automaticSizeText: String = stringResource(id = R.string.auto),
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {

    val fileSize =
        if (gsiDsuObject.fileSize == -1L)
            automaticSizeText
        else gsiDsuObject.fileSize

    Dialog(
        title = title,
        text =
        text.ifEmpty {
            stringResource(
                id = R.string.installation_details,
                gsiDsuObject.name,
                gsiDsuObject.userdataSize,
                fileSize
            )
        },
        confirmText = confirmText,
        cancelText = cancelText,
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() })
}