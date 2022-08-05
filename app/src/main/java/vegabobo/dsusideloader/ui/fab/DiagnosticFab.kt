package vegabobo.dsusideloader.ui.fab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.screen.diaginstallation.FabAction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiagnosticFab(
    fabAction: FabAction,
    onClickStartInstallation: () -> Unit,
    onClickSaveLogs: () -> Unit,
    onLongClickResetInstall: () -> Unit,
) {
    when (fabAction) {
        FabAction.INSTALL -> {
            FloatingActionButton(onClick = onClickStartInstallation) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Launch icon",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(id = R.string.start_installation))
                }
            }
        }
        FabAction.SAVE_LOGS -> {
            FloatingActionButton(
                onClick = onClickSaveLogs,
            ) {
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = onClickSaveLogs,
                            onLongClick = onLongClickResetInstall
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = "Save icon",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(text = stringResource(id = R.string.save_logs))
                    }
                }
            }
        }
        FabAction.LOGS_SAVED -> {
            FloatingActionButton(onClick = {}) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "Done",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(id = R.string.saved_logs))
                }
            }
        }
    }
}