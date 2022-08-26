package vegabobo.dsusideloader.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.BuildConfig
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.screen.about.UpdateStatus
import vegabobo.dsusideloader.ui.screen.about.UpdaterCardState

@Composable
fun UpdaterCard(
    uiState: UpdaterCardState,
    onClickCheckUpdates: () -> Unit,
    onClickDownloadUpdate: () -> Unit,
    onClickViewChangelog: () -> Unit,
) {
    SimpleCard(
        addPadding = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App icon",
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(
                    id = R.string.version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.75f)
            )
        }
        Spacer(modifier = Modifier.padding(10.dp))
        PreferenceItem(
            title = stringResource(id = R.string.check_updates),
            description =
            when (uiState.updateStatus) {
                UpdateStatus.NO_UPDATE_FOUND -> stringResource(id = R.string.running_latest)
                UpdateStatus.UPDATE_FOUND -> stringResource(
                    R.string.update_found,
                    uiState.updateVersion
                )
                else -> stringResource(id = R.string.tap_to_check)
            },
            onClick = { onClickCheckUpdates() }
        )
        AnimatedVisibility(visible = uiState.updateStatus == UpdateStatus.UPDATE_FOUND) {
            Row(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .padding(end = 4.dp)
            ) {
                Spacer(modifier = Modifier.weight(1F))
                ActionButton(
                    text = stringResource(id = R.string.changelog),
                    onClick = { onClickViewChangelog() },
                    colorText = MaterialTheme.colorScheme.primary,
                    colorButton = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )
                ActionButton(
                    text = stringResource(id = R.string.download),
                    onClick = { onClickDownloadUpdate() },
                )
            }
        }
        AnimatedVisibility(visible = uiState.updateStatus == UpdateStatus.CHECKING_FOR_UPDATES) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        AnimatedVisibility(visible = uiState.isDownloading) {
            LinearProgressIndicator(
                progress = uiState.progressBar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}