package vegabobo.dsusideloader.ui.cards

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ActionButton
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.util.LauncherAcResult

@Composable
fun SetupStorage(
    onSetupStorageSuccess: (Uri) -> Unit,
) {

    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    val launcherSetupStorage = LauncherAcResult {
        onSetupStorageSuccess(it)
    }

    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = stringResource(id = R.string.storage),
        text = stringResource(id = R.string.storage_info)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            ActionButton(
                text = stringResource(id = R.string.storage),
                onClick = { launcherSetupStorage.launch(intent) })
        }
    }
}