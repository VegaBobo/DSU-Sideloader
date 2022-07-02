package vegabobo.dsusideloader.util

import android.os.Environment
import android.os.StatFs
import kotlin.math.roundToInt

class StorageUtils {

    companion object {

        fun hasAvailableStorage(): Boolean {
            val statFs = StatFs(Environment.getDataDirectory().absolutePath)
            val blockSize = statFs.blockSizeLong
            val totalSize = statFs.blockCountLong * blockSize
            val availableSize = statFs.availableBlocksLong * blockSize
            return ((availableSize.toFloat() / totalSize.toFloat()) * 100).roundToInt() > 40
        }

    }

}