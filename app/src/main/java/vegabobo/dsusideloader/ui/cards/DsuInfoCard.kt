package vegabobo.dsusideloader.ui.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardWithText

@Composable
fun DsuInfoCard() {
    CardWithText(
        cardTitle = stringResource(id = R.string.what_dsu),
        text = stringResource(id = R.string.dsu_info)
    )
}