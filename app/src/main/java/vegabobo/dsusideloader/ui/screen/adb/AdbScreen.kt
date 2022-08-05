package vegabobo.dsusideloader.ui.screen.adb

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun AdbScreen(
    navController: NavController,
    adbViewModel: AdbViewModel = hiltViewModel()
) {
    val uiState by adbViewModel.uiState.collectAsStateWithLifecycle()
    val scriptPath = adbViewModel.obtainScriptPath()

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
                onClickIcon = { navController.navigate(Destinations.Preferences) },
                onClickBackButton = { adbViewModel.onBackPressed() }
            )
        },
        content = {
            Text(text = stringResource(id = R.string.adb_howto_text))
            SimpleCard(
                text = "sh $scriptPath",
                content = {
                    CopyTextButton(
                        isCopied = uiState.buttonCopyText1,
                        onClickCopy = { adbViewModel.onClickCopyCommand(TargetButton.BTN_COPY_1) }
                    )
                }
            )
            Text(text = stringResource(id = R.string.adb_howto_directly))
            SimpleCard(
                text = "adb shell sh $scriptPath",
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