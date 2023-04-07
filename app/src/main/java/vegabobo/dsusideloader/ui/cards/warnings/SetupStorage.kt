package vegabobo.dsusideloader.ui.cards.warnings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.SimpleCard
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.util.launcherAcResult

@Composable
fun SetupStorage(
    onSetupStorageSuccess: (Uri) -> Unit,
) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    val launcherSetupStorage = launcherAcResult {
        onSetupStorageSuccess(it)
    }

    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        cardTitle = stringResource(id = R.string.setup_storage),
        text = stringResource(id = R.string.setup_storage_description),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            PrimaryButton(
                text = stringResource(id = R.string.setup),
                onClick = { launcherSetupStorage.launch(intent) },
            )
        }
    }
}
