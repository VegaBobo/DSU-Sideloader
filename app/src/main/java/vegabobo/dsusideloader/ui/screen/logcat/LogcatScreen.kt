package vegabobo.dsusideloader.ui.screen.logcat

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.LogcatCard
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.fab.SaveLogsFab
import vegabobo.dsusideloader.ui.util.LauncherAcResult
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun LogcatScreen(
    navController: NavController,
    diagInstViewModel: LogcatViewModel = hiltViewModel()
) {

    val uiState by diagInstViewModel.uiState.collectAsStateWithLifecycle()

    val saveLogsResult = LauncherAcResult {
        diagInstViewModel.onClickSaveLogSuccess(it)
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
        columnContent = false,
        enableDefaultScrollBehavior = false,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.installation_logs),
                scrollBehavior = it,
                showBackButton = true,
                onClickBackButton = { navController.navigateUp() }
            )
        },
        content = {
            LogcatCard(logs = uiState.installationLogs)
        },
        bottomBar = {
            SaveLogsFab(
                onClickSaveLogs = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TITLE, "logs")
                    saveLogsResult.launch(intent)
                }
            )
        }
    )
}