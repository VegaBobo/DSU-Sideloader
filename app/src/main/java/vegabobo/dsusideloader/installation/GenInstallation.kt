package vegabobo.dsusideloader.installation

import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.preparation.StorageManager

class GenInstallation(
    private val GSI: TargetGSI,
    private val storageManager: StorageManager
) {

    object Constants {
        const val INSTALL_SCRIPT_FILENAME = "install"
        const val ASSETS_SCRIPT_FILE = "install_script.sh"
    }

    fun writeInstallScript(isRootMode: Boolean): String {
        return storageManager.writeToFile(getShellScript(isRootMode))
    }

    fun getShellScript(isRooted: Boolean): String {
        return storageManager.readFileFromAssets(Constants.ASSETS_SCRIPT_FILE)
                // on rooted devices, debug mode is managed by app
            .replace("%DEBUG_MODE", if (isRooted) "false" else GSI.debugInstallation.toString())
            .replace("%INSTALLATION_ARGS", genArguments())
            .replace("%INSTALLATION_INFO", installationInfoAsString())
            .replace("%UNMOUNT_SD", GSI.umountSdCard.toString())
    }

    private fun genArguments(): String {
        var arguments = ""
        arguments += addArgument("-d", "${GSI.absolutePath}")
        arguments += addArgument("--el", "KEY_USERDATA_SIZE", GSI.getUserdataInBytes())
        if (GSI.fileSize != -1L)
            arguments += addArgument(
                "--el", "KEY_SYSTEM_SIZE", GSI.fileSize
            )
        return arguments.trim()
    }

    private fun addArgument(argument: String, property: String, value: Any?): String {
        return "$argument $property $value "
    }

    private fun addArgument(argument: String, value: Any?): String {
        return "$argument $value "
    }

    fun installationInfoAsString(): String {
        return "Installation info: " +
                "\nAbsolute path: " + this.GSI.absolutePath +
                "\nFile size: " + this.GSI.fileSize +
                "\nUserdata size: " + this.GSI.userdataSize +
                "\nUnmount: " + this.GSI.umountSdCard +
                "\n\nLogcat:\n"
    }

}