package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OpaqueOverlay(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    alphaFactor: Float,
    overlayColor: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaFactor),
        color = overlayColor
    ) {}

    Box(
        modifier = modifier
            .padding(paddingValues)
            .padding(10.dp)
            .fillMaxSize()
    ) {
        content(this)
    }
}