package vegabobo.dsusideloader.installation

import vegabobo.dsusideloader.core.InstallationPreferences
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.core.StorageManager

object InstallationScript {
    const val FILENAME = "install"
    const val ASSETS_SCRIPT_FILE = "install_script.sh"
}

class GenerateInstallationScript(
    private val storageManager: StorageManager,
    private val instPrefs: InstallationPreferences,
    val gsi: GSI,
) {

    fun writeToFile(): String {
        return storageManager.writeStringToFile(getShellScript(), InstallationScript.FILENAME)
    }

    private fun getShellScript(): String {
        return storageManager.readFileFromAssets(InstallationScript.ASSETS_SCRIPT_FILE)
            .replace("%DEBUG_MODE", instPrefs.isDebugInstallation.toString())
            .replace("%ACTION_INSTALL", InstallationCmdline(gsi).getCmd())
            .replace("%INSTALLATION_INFO", installationInfoAsString())
            .replace("%UNMOUNT_SD", instPrefs.isUnmountSdCard.toString())
    }

    private fun installationInfoAsString(): String {
        return "Installation info: " +
                "\n" +
                "\nAbsolute path: " + gsi.absolutePath +
                "\nFile size: " + gsi.fileSize +
                "\nUserdata size: " + gsi.userdataSize +
                "\nUnmount SD: " + instPrefs.isUnmountSdCard +
                "\n\nLogcat:\n\n"
    }

}