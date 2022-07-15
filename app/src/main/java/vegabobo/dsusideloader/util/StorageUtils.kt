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

        fun maximumAllowedAllocation(): Int {
            val statFs = StatFs(Environment.getDataDirectory().absolutePath)
            val blockSize = statFs.blockSizeLong
            val availableSize = statFs.availableBlocksLong * blockSize
            return ((((availableSize / 1024L) / 1024L) / 1024L) * 0.40).toInt()
        }

    }
}