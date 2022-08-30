package vegabobo.dsusideloader.model

import android.net.Uri
import vegabobo.dsusideloader.installer.privileged.LogcatDiagnostic
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.OperationMode

data class InstallationPreferences(
    var isUnmountSdCard: Boolean = false,
    var useBuiltinInstaller: Boolean = false,
)

class UserSelection(
    var userSelectedUserdata: Long = DSUConstants.DEFAULT_USERDATA,
    var userSelectedImageSize: Long = DSUConstants.DEFAULT_IMAGE_SIZE,
    var selectedFileUri: Uri = Uri.EMPTY,
    var selectedFileName: String = "",
) {

    fun getUserDataSizeAsGB(): String {
        return "${(this.userSelectedUserdata / 1024L / 1024L / 1024L)}"
    }

    fun setUserDataSize(size: String) {
        if (size.isNotEmpty())
            userSelectedUserdata = (FilenameUtils.getDigits(size).toLong()) * 1024L * 1024L * 1024L
    }

    fun setImageSize(size: String) {
        if (size.isNotEmpty())
            userSelectedImageSize = FilenameUtils.getDigits(size).toLong()
    }

    fun isCustomImageSize(): Boolean {
        return userSelectedImageSize != DSUConstants.DEFAULT_IMAGE_SIZE
    }
}

class Session(
    var userSelection: UserSelection = UserSelection(),
    var dsuInstallation: DSUInstallation = DSUInstallation(),
    var preferences: InstallationPreferences = InstallationPreferences(),
    var operationMode: OperationMode = OperationMode.UNROOTED,
    var logger: LogcatDiagnostic? = null,
) {

    // Only populated on UNROOTED mode
    var installationScript = ""

    fun getInstallationParameters(): Triple<Long, String, Long> {
        val userdataSize = userSelection.userSelectedUserdata
        val absoluteFilePath = FilenameUtils.getFilePath(dsuInstallation.uri, true)

        var imageSize = dsuInstallation.fileLength
        if (userSelection.isCustomImageSize())
            imageSize = userSelection.userSelectedImageSize

        return Triple(userdataSize, absoluteFilePath, imageSize)
    }
}