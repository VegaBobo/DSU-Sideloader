package vegabobo.dsusideloader.ui.cards.warnings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton

@Composable
fun RequiresLogPermissionCard(
    onClickGrant: () -> Unit,
    onClickRefuse: () -> Unit,
) {
    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = stringResource(id = R.string.missing_permission),
        text = stringResource(id = R.string.missing_permission_description),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Row {
                SecondaryButton(text = stringResource(id = R.string.refuse), onClick = onClickRefuse)
                Spacer(modifier = Modifier.padding(end = 8.dp))
                PrimaryButton(text = stringResource(id = R.string.grant), onClick = onClickGrant)
            }
        }
    }
}
