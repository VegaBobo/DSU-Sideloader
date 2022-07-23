package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.SimpleCard

@Composable
fun DsuInfoCard(
    modifier: Modifier = Modifier,
    onClickViewDocs: () -> Unit,
    onClickLearnMore: () -> Unit
) {
    SimpleCard(
        modifier = modifier,
        cardTitle = stringResource(id = R.string.what_dsu),
        text = stringResource(id = R.string.dsu_info),
        justifyText = true
    ) {
        Row(modifier = Modifier.padding(top = 6.dp)) {
            Spacer(modifier = Modifier.weight(1F))
            ActionButton(text = stringResource(id = R.string.view_docs), onClick = onClickViewDocs)
            Spacer(modifier = Modifier.padding(start = 10.dp))
            ActionButton(
                text = stringResource(id = R.string.learn_more),
                onClick = onClickLearnMore
            )
        }
    }
}