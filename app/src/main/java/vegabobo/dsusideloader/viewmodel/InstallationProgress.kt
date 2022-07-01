package vegabobo.dsusideloader.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import vegabobo.dsusideloader.R

class InstallationProgress(
    val currentProgress: MutableState<Int> = mutableStateOf(-1)
) {

    object Steps {
        const val FINISHED = 0
        const val COPYING_FILE = 1 // -> c.getString(R.string.gz_copy)
        const val DECOMPRESSING_XZ = 2 // -> c.getString(R.string.gz_copy)
        const val COMPRESSING_TO_GZ = 3 // -> c.getString(R.string.compressing_img_to_gzip)
        const val DECOMPRESSING_GZIP = 4 // -> c.getString(R.string.extracting_gzip)
    }

    fun set(value: Int) {
        this.currentProgress.value = value
    }

    fun getProgress(): Int {
        return this.currentProgress.value
    }

    fun getProgress(context: Context): String {
        return when (getProgress()){
            Steps.FINISHED -> context.getString(R.string.done)
            Steps.COPYING_FILE -> context.getString(R.string.copying_file)
            Steps.DECOMPRESSING_XZ -> context.getString(R.string.extracting_xz)
            Steps.COMPRESSING_TO_GZ -> context.getString(R.string.compressing_img_to_gzip)
            Steps.DECOMPRESSING_GZIP -> context.getString(R.string.extracting_gzip)
            else -> { context.getString(R.string.error) }
        }
    }

    fun isFinished(): Boolean{
        return getProgress()==Steps.FINISHED
    }

}
