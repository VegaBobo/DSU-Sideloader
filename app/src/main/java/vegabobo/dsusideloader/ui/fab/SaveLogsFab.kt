package vegabobo.dsusideloader.ui.fab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R

@Composable
fun SaveLogsFab(
    onClickSaveLogs: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        FloatingActionButton(
            onClick = onClickSaveLogs,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = "Save icon",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = stringResource(id = R.string.save_logs))
            }
        }
    }
}