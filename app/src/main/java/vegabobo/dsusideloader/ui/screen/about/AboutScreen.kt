package vegabobo.dsusideloader.ui.screen.about

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.updater.UpdaterCard
import vegabobo.dsusideloader.ui.components.*
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

object AboutLinks {
    const val CONTRIBUTORS_URL = "https://github.com/VegaBobo/DSU-Sideloader/graphs/contributors"
    const val REPOSITORY_URL = "https://github.com/VegaBobo/DSU-Sideloader"
}

@Composable
fun AboutScreen(
    navController: NavController,
    aboutViewModel: AboutViewModel = hiltViewModel()
) {

    val uiState by aboutViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

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
            onClickViewChangelog = { uriHandler.openUri(aboutViewModel.response.changelogUrl) }
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
                description = stringResource(id = R.string.github_repo_description),
                onClick = { uriHandler.openUri(AboutLinks.REPOSITORY_URL) }
            )
            PreferenceItem(
                title = stringResource(id = R.string.libraries_title),
                description = stringResource(id = R.string.libraries_description),
                onClick = { navController.navigate(Destinations.Libraries) }
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
                description = stringResource(id = R.string.role_developer),
                onClick = {}
            )
            PreferenceItem(
                title = "WSTxda",
                description = stringResource(id = R.string.role_design_icon),
                onClick = {}
            )
            if (stringResource(id = R.string.translators_list).isNotEmpty())
                PreferenceItem(
                    title = stringResource(id = R.string.translators_title),
                    description = stringResource(id = R.string.translators_list)
                )
            PreferenceItem(
                title = stringResource(id = R.string.contributors_title),
                description = stringResource(id = R.string.contributors_text),
                onClick = { uriHandler.openUri(AboutLinks.CONTRIBUTORS_URL) }
            )
        }
    }
}