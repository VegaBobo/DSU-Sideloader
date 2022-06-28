package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = {},
    content: @Composable () -> Unit = {},
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(scrollBehavior) },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = verticalArrangement,
            ) {
                content()
            }
        }

    )
}