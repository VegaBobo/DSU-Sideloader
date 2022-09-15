package vegabobo.dsusideloader.ui.components.bsheets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.LogcatCard
import vegabobo.dsusideloader.ui.components.DialogLikeBottomSheet
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.util.LauncherAcResult

@Composable
fun ViewLogsBottomSheet(
    logs: String,
    onClickSaveLogs: (Uri) -> Unit,
    onClose: () -> Unit,
) {
    val saveLogsResult = LauncherAcResult {
        onClickSaveLogs(it)
    }

    DialogLikeBottomSheet(
        title = stringResource(id = R.string.installation_logs),
        icon = Icons.Outlined.Description,
        onClose = onClose
    ) {
        LogcatCard(logs = logs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, end = 12.dp, bottom = 14.dp)
        ) {
            Spacer(modifier = Modifier.weight(1F))
            PrimaryButton(
                text = stringResource(id = R.string.save_logs),
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TITLE, "logs")
                    saveLogsResult.launch(intent)
                }
            )
        }
    }
}
