package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.Dialog

@Composable
fun ConfirmInstallationDialog(
    dialogText: String = "",
    filename: String,
    userdata: String,
    fileSize: Long,
    isCustomImageSize: Boolean,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {
    Dialog(
        title = stringResource(id = R.string.info),
        icon = Icons.Outlined.InstallMobile,
        text =
        dialogText.ifEmpty {
            stringResource(
                id = R.string.installation_details,
                filename,
                userdata,
                if (isCustomImageSize) "${fileSize}b" else stringResource(id = R.string.auto)
            )
        },
        confirmText = stringResource(id = R.string.proceed),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() })
}