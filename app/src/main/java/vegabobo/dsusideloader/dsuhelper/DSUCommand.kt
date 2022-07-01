package vegabobo.dsusideloader.dsuhelper

import android.content.Context
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils
import vegabobo.dsusideloader.util.WorkspaceUtils

class DSUCommand(
    private val GSI: GSI, val context: Context, private val skipDebugMode: Boolean
) {

    object Constants {
        const val INSTALL_SCRIPT_FILENAME = "install"
        const val ASSETS_SCRIPT_FILE = "install_script.sh"
    }

    private val script: String

    init {
        script = getInstallScript()
    }

    fun getInstallScript(): String {
        return String.format(
            getShellScriptFromAssets(), getDebugMode(), genArguments(), installationInfoAsString()
        )
    }

    fun writeInstallScript(): String {
        val workspaceFolder = WorkspaceUtils.getWorkspaceFolder(context)
        val d = workspaceFolder.createFile("text/x-shellscript", Constants.INSTALL_SCRIPT_FILENAME)
        val scriptFile = FilenameUtils.getFilePath(d!!.uri, false).replace("file://", "")
        val outputStream = context.contentResolver.openOutputStream(d.uri)
        val strToBytes = getInstallScript().toByteArray()
        outputStream!!.write(strToBytes)
        outputStream.close()
        return scriptFile
    }

    private fun getShellScriptFromAssets(): String {
        return context.assets.open(Constants.ASSETS_SCRIPT_FILE).bufferedReader()
            .use { it.readText() }
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

    private fun getDebugMode(): Boolean {
        return if (skipDebugMode)
            false
        else
            SPUtils.isDebugModeEnabled(context)
    }

    fun installationInfoAsString():String{
        return "Installation info: " +
        "\nAbsolute path: " + this.GSI.absolutePath +
        "\nFile size: " + this.GSI.fileSize +
        "\nUserdata size: " + this.GSI.userdataSize +
        "\n\nLogcat:\n"
    }

}