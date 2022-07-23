package vegabobo.dsusideloader.ui.screens.adb

import android.util.Base64
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun AdbScreen(
    navController: NavController,
    cmd: String? = "",
    adbViewModel: AdbViewModel = viewModel()
) {
    val decodedText = String(Base64.decode(cmd, Base64.DEFAULT))
    val uiState by adbViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isShowingExitDialog) {
        Dialog(
            title = stringResource(id = R.string.installation),
            text = stringResource(id = R.string.return_to_home),
            confirmText = stringResource(id = R.string.yes),
            cancelText = stringResource(id = R.string.no),
            onClickConfirm = { adbViewModel.onClickConfirmClose() },
            onClickCancel = { adbViewModel.onClickCancelDialog() }
        )
    }

    LaunchedEffect(key1 = Unit) {
        adbViewModel.navigateBack.collectLatest {
            if (adbViewModel.navigateBack.value) {
                navController.navigateUp()
            }
        }
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.ready_to_install),
                scrollBehavior = it,
                showBackButton = true,
                onClickIcon = { navController.navigate(Destinations.Settings) },
                onClickBackButton = { adbViewModel.onBackPressed() }
            )
        },
        content = {
            Text(text = stringResource(id = R.string.adb_howto_text))
            SimpleCard(
                text = "sh $decodedText",
                content = {
                    CopyTextButton(
                        isCopied = uiState.buttonCopyText1,
                        onClickCopy = { adbViewModel.onClickCopyCommand(TargetButton.BTN_COPY_1) }
                    )
                }
            )
            Text(text = stringResource(id = R.string.adb_howto_directly))
            SimpleCard(
                text = "adb shell sh $decodedText",
                content = {
                    CopyTextButton(
                        isCopied = uiState.buttonCopyText2,
                        onClickCopy = { adbViewModel.onClickCopyCommand(TargetButton.BTN_COPY_2) }
                    )
                }
            )
            Text(text = stringResource(id = R.string.adb_howto_done))
        }
    )
    BackHandler {
        if (!uiState.isShowingExitDialog)
            adbViewModel.onBackPressed()
    }
}