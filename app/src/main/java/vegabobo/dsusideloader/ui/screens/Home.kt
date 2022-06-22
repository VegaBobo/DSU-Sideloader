package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.components.cards.ImageSizeCard
import vegabobo.dsusideloader.ui.components.cards.InfoCard
import vegabobo.dsusideloader.ui.components.cards.InstallationCard
import vegabobo.dsusideloader.ui.components.cards.UserdataCard
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController, insets: PaddingValues) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {

        val decayAnimationSpec = rememberSplineBasedDecay<Float>()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec,
            rememberTopAppBarScrollState()
        )

        var userdataToggle by remember { mutableStateOf(false) }
        var imageSize by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBar(
                    title = stringResource(id = R.string.app_name),
                    showIcon = true,
                    icon = Icons.Outlined.Settings,
                    scrollBehavior = scrollBehavior,
                ) {
                    navController.navigate(Destinations.Settings)
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(14.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InstallationCard(
                        onClickClear = {},
                        onClickInstall = {},
                        onClickTextField = {}
                    )
                    Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                    UserdataCard(
                        addToggle = true,
                        isToggleEnabled = userdataToggle,
                        isError = false,
                        onCheckedChange = {
                            userdataToggle = !userdataToggle
                        })
                    Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                    ImageSizeCard(
                        addToggle = true,
                        isToggleEnabled = imageSize,
                        isError = false,
                        onCheckedChange = {
                            imageSize = !imageSize
                        })
                    Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                    InfoCard()
                }
            }

        )
    }

}