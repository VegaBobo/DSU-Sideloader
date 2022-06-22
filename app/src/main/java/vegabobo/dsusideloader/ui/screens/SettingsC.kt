package vegabobo.dsusideloader.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.components.cards.ImageSizeCard
import vegabobo.dsusideloader.ui.components.cards.InfoCard
import vegabobo.dsusideloader.ui.components.cards.InstallationCard
import vegabobo.dsusideloader.ui.components.cards.UserdataCard
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme

@Composable
fun SettingsC(navController: NavController, insets: PaddingValues) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {

    }

}