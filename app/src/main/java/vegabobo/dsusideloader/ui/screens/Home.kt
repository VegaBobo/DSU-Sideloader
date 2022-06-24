package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.components.cards.ImageSizeCard
import vegabobo.dsusideloader.ui.components.cards.InfoCard
import vegabobo.dsusideloader.ui.components.cards.InstallationCard
import vegabobo.dsusideloader.ui.components.cards.UserdataCard
import vegabobo.dsusideloader.viewmodel.HomeViewModel
import vegabobo.dsusideloader.viewmodel.Toggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState()
    )

    val userdataToggle by homeViewModel.userdataToggle.observeAsState(false)
    val imageSizeToggle by homeViewModel.imageSizeToggle.observeAsState(false)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
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
                    onClickInstall = { },
                    onClickTextField = { homeViewModel.onClickSelectFile() }
                )
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                UserdataCard(
                    addToggle = true,
                    isToggleEnabled = userdataToggle,
                    isError = false,
                    onCheckedChange = {
                        homeViewModel.onTouchToggle(Toggle.USERDATA_TOGGLE)
                    })
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                ImageSizeCard(
                    addToggle = true,
                    isToggleEnabled = imageSizeToggle,
                    isError = false,
                    onCheckedChange = {
                        homeViewModel.onTouchToggle(Toggle.IMGSIZE_TOGGLE)
                    })
                Spacer(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                InfoCard()
            }
        }

    )

}