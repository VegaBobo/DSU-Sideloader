package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogcatCard(
    logs: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.inverseOnSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(400.dp),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = logs,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
