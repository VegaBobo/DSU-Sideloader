package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SnackBar(
    text: String,
    textButton: String,
    onClickButton: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
//            .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(14.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onBackground)
        Text(
            text = textButton,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClickButton() }
                .padding(6.dp)
        )
    }
}
