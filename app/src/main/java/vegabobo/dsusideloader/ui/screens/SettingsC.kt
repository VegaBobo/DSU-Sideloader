package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title
import vegabobo.dsusideloader.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsC(navController: NavController) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.preferences),
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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

    )


}