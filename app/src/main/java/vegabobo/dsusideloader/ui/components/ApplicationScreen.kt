package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = {},
    outsideContent: @Composable (PaddingValues) -> Unit = {},
    content: @Composable () -> Unit = {},
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState()
    )

    val insets = WindowInsets
        .systemBars
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

    Surface(
        modifier = Modifier.padding(insets)
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            topBar = { topBar(scrollBehavior) },
            content = { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = verticalArrangement,
                ) {
                    content()
                }
            }
        )
    }

    outsideContent(insets)

}

