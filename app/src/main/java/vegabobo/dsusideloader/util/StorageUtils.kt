package vegabobo.dsusideloader.util

import android.os.Environment
import android.os.StatFs
import kotlin.math.roundToInt

class StorageUtils {

    companion object {

        fun getAllocInfo(allowedPercentage: Float): Pair<Boolean, Int> {
            val statFs = StatFs(Environment.getDataDirectory().absolutePath)
            val blockSize = statFs.blockSizeLong
            val totalSize = statFs.blockCountLong * blockSize
            val availableSize = statFs.availableBlocksLong * blockSize
            val hasAvailableStorage =
                (availableSize.toFloat() / totalSize.toFloat() * 100).roundToInt() > allowedPercentage
            val maximumAllowedForAllocation =
                ((availableSize / 1024L / 1024L / 1024L) * allowedPercentage).toInt()
            return Pair(hasAvailableStorage, maximumAllowedForAllocation)
        }
    }
}
