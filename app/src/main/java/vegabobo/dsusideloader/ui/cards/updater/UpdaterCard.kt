package vegabobo.dsusideloader.ui.cards.updater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.BuildConfig
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton
import vegabobo.dsusideloader.ui.screen.about.UpdateStatus
import vegabobo.dsusideloader.ui.screen.about.UpdaterCardState

@Composable
fun UpdaterCard(
    uiState: UpdaterCardState,
    isUpdaterAvailable: Boolean,
    onClickImage: () -> Unit,
    onClickCheckUpdates: () -> Unit,
    onClickDownloadUpdate: () -> Unit,
    onClickViewChangelog: () -> Unit,
) {
    fun isDownloading(): Boolean =
        uiState.isDownloading || uiState.updateStatus == UpdateStatus.CHECKING_FOR_UPDATES

    fun isCheckingForUpdates(): Boolean =
        uiState.updateStatus == UpdateStatus.CHECKING_FOR_UPDATES

    fun isUpdateFound(): Boolean =
        uiState.updateStatus == UpdateStatus.UPDATE_FOUND

    SimpleCard(
        addPadding = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface,
            ) {
                Box {
                    val progressBarModifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                    if (isCheckingForUpdates()) {
                        CircularProgressIndicator(modifier = progressBarModifier)
                    }
                    if (uiState.isDownloading) {
                        CircularProgressIndicator(
                            progress = uiState.progressBar,
                            modifier = progressBarModifier,
                        )
                    }

                    val selected = remember { mutableStateOf(false) }
                    val scale = animateFloatAsState(if (selected.value) 0.75f else 1f)
                    selected.value = isDownloading()
                    Image(
                        modifier = Modifier
                            .size(96.dp)
                            .scale(scale.value)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                            .clickable { onClickImage() },
                        painter = painterResource(id = R.drawable.app_icon_mini),
                        contentDescription = "App icon",
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 22.sp,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(
                    id = R.string.version_info,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                ),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.75f),
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        if (isUpdaterAvailable) {
            PreferenceItem(
                title = stringResource(id = R.string.check_updates_title),
                description =
                when (uiState.updateStatus) {
                    UpdateStatus.NO_UPDATE_FOUND ->
                        stringResource(id = R.string.check_updates_text_updated)

                    UpdateStatus.UPDATE_FOUND ->
                        stringResource(R.string.check_updates_text_found, uiState.updateVersion)

                    else ->
                        stringResource(id = R.string.check_updates_text_idle)
                },
                onClick = { onClickCheckUpdates() },
            )
            AnimatedVisibility(visible = isUpdateFound()) {
                Row(
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .padding(end = 4.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1F))
                    SecondaryButton(
                        text = stringResource(id = R.string.changelog),
                        onClick = { onClickViewChangelog() },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    PrimaryButton(
                        text = stringResource(id = R.string.download),
                        onClick = { onClickDownloadUpdate() },
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.padding(6.dp))
        }
    }
}
