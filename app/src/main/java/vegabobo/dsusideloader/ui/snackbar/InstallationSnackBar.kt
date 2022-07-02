package vegabobo.dsusideloader.ui.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.components.OpaqueOverlay
import vegabobo.dsusideloader.ui.components.SnackBar

@Composable
fun InstallationSnackBar(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    alphaFactor: Float = 0.3F,
    text: String,
    textButton: String,
    onClickButton: () -> Unit,
    showProgressIndicator: Boolean = false
) {
    OpaqueOverlay(paddingValues = paddingValues, alphaFactor = alphaFactor) {
        Box(modifier = Modifier.align(Alignment.BottomStart)) {
            Column {
                SnackBar(
                    text = text,
                    textButton = textButton,
                    onClickButton = { onClickButton() },
                    showProgressIndicator = showProgressIndicator
                )
            }
        }
    }
}
