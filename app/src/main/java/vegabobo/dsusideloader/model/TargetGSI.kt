package vegabobo.dsusideloader.model

import android.net.Uri
import vegabobo.dsusideloader.util.FilenameUtils

class TargetGSI(
    var absolutePath: String = "",
    var targetUri: Uri = Uri.EMPTY,
    var name: String = "",
    var fileSize: Long = Constants.DEFAULT_FILE_SIZE,
    var userdataSize: Int = Constants.DEFAULT_USERDATA_SIZE_IN_GB,
    var debugInstallation: Boolean = false,
    var umountSdCard: Boolean = false
) {

    object Constants {
        const val DEFAULT_FILE_SIZE = -1L // bytes
        const val DEFAULT_USERDATA_SIZE_IN_GB = 2 // gigabytes
    }

    fun getUserdataInBytes(): Long {
        return userdataSize.toLong() * 1024L * 1024L * 1024L
    }

    fun setFileSize(size: String) {
        if (size.isNotEmpty())
            this.fileSize = FilenameUtils.getDigits(size).toLong()
    }

    fun setUserdataSize(size: String) {
        if (size.isNotEmpty())
            this.userdataSize = FilenameUtils.getDigits(size).toInt()
    }

}
