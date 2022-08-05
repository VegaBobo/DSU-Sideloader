package vegabobo.dsusideloader.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preparation.PreparationSteps

@Composable
fun InstallationText(
    step: PreparationSteps
): String {
    return when (step) {
        PreparationSteps.COPYING_FILE -> stringResource(R.string.gz_copy)
        PreparationSteps.DECOMPRESSING_XZ -> stringResource(R.string.extracting_gzip)
        PreparationSteps.COMPRESSING_TO_GZ -> stringResource(R.string.compressing_img_to_gzip)
        PreparationSteps.DECOMPRESSING_GZIP -> stringResource(R.string.extracting_gzip)
        PreparationSteps.FINISHED -> stringResource(R.string.done)
        else -> stringResource(R.string.processing)
    }
}