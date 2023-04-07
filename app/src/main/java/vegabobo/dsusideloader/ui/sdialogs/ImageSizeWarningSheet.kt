package vegabobo.dsusideloader.ui.sdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.DialogLikeBottomSheet

@Composable
fun ImageSizeWarningSheet(
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {
    DialogLikeBottomSheet(
        title = stringResource(id = R.string.dialog_image_size),
        icon = Icons.Outlined.Edit,
        text = stringResource(id = R.string.dialog_image_size_description),
        confirmText = stringResource(id = R.string.set_anyway),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = onClickConfirm,
        onClickCancel = onClickCancel,
    )
}
