package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardTitle(cardTitle: String){
    Text(
        text = cardTitle,
        fontSize = 20.sp,
        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
    )
}