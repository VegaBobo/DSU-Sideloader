package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
    showProgressIndicator: Boolean = false
) {
    val shape =
        if (showProgressIndicator)
            RoundedCornerShape(bottomEnd = 6.dp, bottomStart = 6.dp)
        else
            RoundedCornerShape(6.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.inverseOnSurface)
    ) {
        if (showProgressIndicator)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Column(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
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
}
