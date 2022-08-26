package vegabobo.dsusideloader.ui.screen.about

import androidx.compose.foundation.layout.*
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
import vegabobo.dsusideloader.ui.cards.UpdaterCard
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun AboutScreen(
    navController: NavController,
    aboutViewModel: AboutViewModel = hiltViewModel()
) {

    val uiState by aboutViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        aboutViewModel.aboutViewAction.collectLatest {
            when (it) {
                AboutViewAction.NAVIGATE_TO_LIBRARIES ->
                    navController.navigate(Destinations.Libraries)
                else -> {}
            }
            aboutViewModel.resetViewAction()
        }
    }

    ApplicationScreen(
        modifier = Modifier.padding(10.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.about),
                scrollBehavior = it,
                showBackButton = true,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    ) {
        UpdaterCard(
            uiState = uiState.updaterCardState,
            onClickCheckUpdates = { aboutViewModel.onClickCheckUpdates() },
            onClickDownloadUpdate = { aboutViewModel.onClickDownloadUpdate() },
            onClickViewChangelog = { aboutViewModel.onClickViewChangelog() }
        )
        Title(
            stringResource(id = R.string.application),
            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
        )
        SimpleCard(
            addPadding = false
        ) {
            PreferenceItem(
                title = stringResource(id = R.string.github_repo),
                description = stringResource(id = R.string.github_sauce),
                onClick = { aboutViewModel.onClickViewRepository() }
            )
            PreferenceItem(
                title = stringResource(id = R.string.libraries),
                description = stringResource(id = R.string.libraries_used),
                onClick = { aboutViewModel.onClickViewLibraries() }
            )
        }
        Title(
            stringResource(id = R.string.collaborators),
            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
        )
        SimpleCard(
            addPadding = false
        ) {
            PreferenceItem(
                title = "VegaBobo",
                description = stringResource(id = R.string.developer),
                onClick = {}
            )
            PreferenceItem(
                title = "WSTxda",
                description = stringResource(id = R.string.design_and_icon),
                onClick = {}
            )
            if (stringResource(id = R.string.translator_list).isNotEmpty())
                PreferenceItem(
                    title = stringResource(id = R.string.translators),
                    description = stringResource(id = R.string.translator_list)
                )
            PreferenceItem(
                title = stringResource(id = R.string.contributors),
                description = stringResource(id = R.string.contribuitors_desc),
                onClick = { aboutViewModel.onClickViewContribuitors() }
            )
        }
    }
}