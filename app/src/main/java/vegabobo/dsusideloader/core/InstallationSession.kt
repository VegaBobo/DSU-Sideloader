package vegabobo.dsusideloader.core

import kotlinx.coroutines.Job
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.OperationMode

data class InstallationPreferences(
    var isDebugInstallation: Boolean = false,
    var isUnmountSdCard: Boolean = false,
)

class InstallationSession(
    // GSI that will be installed via DSU
    var gsi: GSI = GSI(),

    // Only filename from file user selected (like: system.img)
    var selectedFilename: String = "",

    // Holds installation preferences
    var preferences: InstallationPreferences = InstallationPreferences(),

    // Installation Job
    var job: Job = Job(),

    // Session's operation mode
    var operationMode: OperationMode = OperationMode.UNROOTED,

    // script file path (only used when operation mode == non rooted)
    // otherwise, will be blank
    var installationScriptFilePath: String = ""
) {

    fun reset() {
        gsi = GSI()
        newJob()
    }

    fun newJob() {
        if (isSessionActive())
            cancelJob()
        job = Job()
    }

    fun cancelJob() {
        job.cancel()
    }

    fun isSessionActive(): Boolean {
        return job.isActive
    }

    fun setGsiFileSize(size: String) {
        if (size.isNotEmpty())
            gsi.fileSize = FilenameUtils.getDigits(size).toLong()
    }

    fun setGsiUserdataSize(size: String) {
        if (size.isNotEmpty())
            gsi.userdataSize = (FilenameUtils.getDigits(size).toLong()) * 1024L * 1024L * 1024L
    }

    fun getTargetFileExtension(): String {
        return selectedFilename.substring(selectedFilename.lastIndexOf("."))
    }

    fun getTargetFilename(): String {
        return selectedFilename.split(".")[0]
    }

    fun isCustomFileSize(): Boolean {
        return gsi.fileSize != GSI.GSIConstants.DEFAULT_FILE_SIZE
    }

}