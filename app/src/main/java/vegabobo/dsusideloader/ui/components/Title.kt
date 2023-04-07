package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .padding(start = 17.dp)
            .padding(bottom = 8.dp)
            .padding(top = 8.dp),
    )
}
