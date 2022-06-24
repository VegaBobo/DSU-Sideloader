package vegabobo.dsusideloader.ui.components.cards

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox

@Composable
fun InfoCard(
    cardTitle: String = stringResource(id = R.string.what_dsu),
    text: String = stringResource(id = R.string.dsu_info),
    addToggle: Boolean = false,
    isToggleEnabled: Boolean = false,
) {
    CardBox(
        cardTitle = cardTitle,
        addToggle = addToggle,
        isToggleEnabled = isToggleEnabled,
    ) {
        Text(text = text, modifier = Modifier.padding(top = 10.dp))
    }
}