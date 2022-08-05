package vegabobo.dsusideloader.model

import android.net.Uri

class GSI(
    var absolutePath: String = "",
    var uri: Uri = Uri.EMPTY,
    var fileSize: Long = GSIConstants.DEFAULT_FILE_SIZE,
    var userdataSize: Long = GSIConstants.DEFAULT_USERDATA_SIZE_IN_GB
) {
    object GSIConstants {
        const val DEFAULT_FILE_SIZE = -1L // bytes
        const val DEFAULT_USERDATA_SIZE_IN_GB = 2L * 1024L * 1024L * 1024L // gigabytes
    }
    fun obtainUserdataInGb():Int = (userdataSize / 1024L / 1024L / 1024L).toInt()
}