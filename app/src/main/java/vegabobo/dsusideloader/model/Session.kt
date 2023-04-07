package vegabobo.dsusideloader.model

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun getUserDataSizeAsGB(): String = "${(this.userSelectedUserdata / 1024L / 1024L / 1024L)}"

    fun setUserDataSize(size: String) {
        userSelectedUserdata =
            if (size.isNotEmpty()) {
                (FilenameUtils.getDigits(size).toLong()) * 1024L * 1024L * 1024L
            } else {
                DSUConstants.DEFAULT_USERDATA
            }
    }

    fun setImageSize(size: String) {
        userSelectedImageSize =
            if (size.isNotEmpty()) {
                FilenameUtils.getDigits(size).toLong()
            } else {
                DSUConstants.DEFAULT_IMAGE_SIZE
            }
    }

    fun isCustomImageSize(): Boolean {
        return userSelectedImageSize != DSUConstants.DEFAULT_IMAGE_SIZE
    }

    override fun toString(): String {
        return "UserSelection(userSelectedUserdata=$userSelectedUserdata, " +
            "userSelectedImageSize=$userSelectedImageSize, " +
            "selectedFileUri=$selectedFileUri, " +
            "selectedFileName=$selectedFileName)"
    }
}

class Session(
    var userSelection: UserSelection = UserSelection(),
    var dsuInstallation: DSUInstallationSource = DSUInstallationSource(),
    var preferences: InstallationPreferences = InstallationPreferences(),
    var operationMode: MutableStateFlow<OperationMode> = MutableStateFlow(OperationMode.ADB),
) {

    fun isRoot(): Boolean {
        return (
            operationMode.value == OperationMode.SYSTEM_AND_ROOT ||
                operationMode.value == OperationMode.ROOT
            )
    }

    fun getOperationMode() = operationMode.value
    fun setOperationMode(newOpMode: OperationMode) {
        operationMode.value = newOpMode
    }

    // Only populated on UNROOTED mode
    var installationScriptPath = ""

    fun getInstallationParameters(): Triple<Long, String, Long> {
        val userdataSize = userSelection.userSelectedUserdata
        val absoluteFilePath = FilenameUtils.getFilePath(dsuInstallation.uri, true)

        var imageSize = dsuInstallation.fileSize
        if (userSelection.isCustomImageSize()) {
            imageSize = userSelection.userSelectedImageSize
        }
        return Triple(userdataSize, absoluteFilePath, imageSize)
    }

    override fun toString(): String {
        return "$userSelection\n$dsuInstallation\n$preferences\noperationMode: ${operationMode.value}"
    }
}
