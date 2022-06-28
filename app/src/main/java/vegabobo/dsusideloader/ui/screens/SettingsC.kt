package vegabobo.dsusideloader.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title
import vegabobo.dsusideloader.ui.components.TopBar

@Composable
fun SettingsC(navController: NavController) {

    ApplicationScreen(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.preferences),
                icon = null,
                scrollBehavior = it,
            )
        }
    ) {
        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.op_mode),
            description = stringResource(
                id = R.string.placeholder
            ),
            onClick = {}
        )
        PreferenceItem(
            title = stringResource(id = R.string.debug_installation),
            description = stringResource(
                id = R.string.debug_installation_text
            ),
            onClick = {},
            onCheckSwitch = {}
        )
        PreferenceItem(
            title = stringResource(id = R.string.keep_screen_on),
            onClick = {},
            onCheckSwitch = {}
        )
        Title(title = stringResource(id = R.string.other))
        PreferenceItem(
            title = stringResource(id = R.string.about),
            onClick = {}
        )
    }

}