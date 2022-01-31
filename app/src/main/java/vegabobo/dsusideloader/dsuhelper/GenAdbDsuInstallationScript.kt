package vegabobo.dsusideloader.dsuhelper

import android.content.Context
import android.util.Log
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils
import vegabobo.dsusideloader.util.WorkspaceFilesUtils

class GenAdbDsuInstallationScript(
    private val gsiDsuObject: GsiDsuObject,
    private val context: Context
) {

    object Constants {
        const val INSTALL_SCRIPT_FILENAME = "install"
        const val ASSETS_SCRIPT_FILE = "install_script.sh"
    }

    fun generateScriptFile(): String {
        val workspaceFolder = WorkspaceFilesUtils.getWorkspaceFolder(context)
        val d = workspaceFolder.createFile("text/x-shellscript", Constants.INSTALL_SCRIPT_FILENAME)
        val scriptFile =
            FilenameUtils.getFilePath(d!!.uri, false).replace("file://", "")
        val str = String.format(
            getShellScriptFromAssets(),
            gsiDsuObject.absolutePath,
            gsiDsuObject.fileSize,
            gsiDsuObject.getUserdataInBytes(),
            SPUtils.isDebugModeEnabled(context).toString(),
            scriptFile
        )
        val outputStream = context.contentResolver.openOutputStream(d.uri)
        val strToBytes = str.toByteArray()
        outputStream!!.write(strToBytes)
        outputStream.close()
        return scriptFile
    }

    private fun getShellScriptFromAssets(): String {
        return context.assets.open(Constants.ASSETS_SCRIPT_FILE).bufferedReader()
            .use { it.readText() }
    }

}
