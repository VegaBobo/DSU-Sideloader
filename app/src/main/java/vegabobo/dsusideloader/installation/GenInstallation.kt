package vegabobo.dsusideloader.installation

import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.preparation.StorageManager

class GenInstallation(
    private val GSI: TargetGSI,
    private val storageManager: StorageManager,
    private val skipDebugMode: Boolean,
) {

    object Constants {
        const val INSTALL_SCRIPT_FILENAME = "install"
        const val ASSETS_SCRIPT_FILE = "install_script.sh"
    }

    fun getInstallScript(): String {
        return String.format(
            getShellScriptFromAssets(),
            getDebugMode(GSI.debugInstallation),
            genArguments(),
            installationInfoAsString()
        )
    }

    fun writeInstallScript(): String {
        return storageManager.writeToFile(getShellScriptFromAssets())
    }

    private fun getShellScriptFromAssets(): String {
        return storageManager.readFileFromAssets(Constants.ASSETS_SCRIPT_FILE)
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

    private fun getDebugMode(isDebugMode: Boolean): Boolean {
        return if (skipDebugMode)
            false
        else
            isDebugMode
    }

    fun installationInfoAsString(): String {
        return "Installation info: " +
                "\nAbsolute path: " + this.GSI.absolutePath +
                "\nFile size: " + this.GSI.fileSize +
                "\nUserdata size: " + this.GSI.userdataSize +
                "\n\nLogcat:\n"
    }

}