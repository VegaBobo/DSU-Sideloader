package vegabobo.dsusideloader.ui.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.model.DSUConstants
import vegabobo.dsusideloader.ui.components.Dialog
import vegabobo.dsusideloader.ui.components.DialogItem

@Composable
fun ConfirmInstallationDialog(
    filename: String,
    userdata: String,
    fileSize: Long,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {
    Dialog(
        title = stringResource(id = R.string.proceed_installation_title),
        icon = Icons.Outlined.InstallMobile,
        text = stringResource(id = R.string.proceed_installation_text),
        content = {
            Divider(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .alpha(0.50f),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 1.dp,
            )
            DialogItem(
                icon = Icons.Outlined.InsertDriveFile,
                title = stringResource(id = R.string.selected_file_n),
                text = filename
            )
            DialogItem(
                icon = Icons.Outlined.Storage,
                title = stringResource(id = R.string.userdata_size_nn),
                text = "${userdata}GB"
            )
            if (fileSize != DSUConstants.DEFAULT_IMAGE_SIZE)
                DialogItem(
                    icon = Icons.Outlined.Article,
                    title = stringResource(id = R.string.image_size_n),
                    text = "${fileSize}b"
                )
            Divider(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .alpha(0.50f),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 1.dp,
            )
        },
        confirmText = stringResource(id = R.string.proceed),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() })
}