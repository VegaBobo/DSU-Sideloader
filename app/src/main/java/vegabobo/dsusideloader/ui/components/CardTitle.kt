package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun CardTitle(modifier: Modifier = Modifier, cardTitle: String) {
    val scroll = rememberScrollState(0)
    Text(
        modifier = modifier.horizontalScroll(scroll),
        text = cardTitle,
        fontSize = 20.sp,
        maxLines = 1,
        style = MaterialTheme.typography.headlineMedium,
    )
}
