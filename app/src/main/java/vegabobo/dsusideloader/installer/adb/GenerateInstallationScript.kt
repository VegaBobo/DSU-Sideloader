package vegabobo.dsusideloader.installer.adb

import vegabobo.dsusideloader.model.InstallationPreferences
import vegabobo.dsusideloader.core.StorageManager

object InstallationScript {
    const val FILENAME = "install"
    const val ASSETS_SCRIPT_FILE = "install_script.sh"
}

class GenerateInstallationScript(
    private val storageManager: StorageManager,
    private val parameters: Triple<Long, String, Long>,
    private val instPrefs: InstallationPreferences = InstallationPreferences(),
) {

    fun writeToFile(): String {
        return storageManager.writeStringToFile(getShellScript(), InstallationScript.FILENAME)
    }

    private fun getShellScript(): String {
        return storageManager.readFileFromAssets(InstallationScript.ASSETS_SCRIPT_FILE)
            .replace("%ACTION_INSTALL", InstallationCmdline(parameters).getCmd())
            .replace("%UNMOUNT_SD", instPrefs.isUnmountSdCard.toString())
    }

}