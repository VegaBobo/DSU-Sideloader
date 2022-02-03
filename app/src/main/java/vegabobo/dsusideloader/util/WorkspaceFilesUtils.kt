package vegabobo.dsusideloader.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream

class WorkspaceFilesUtils {

    companion object {

        fun getWorkspaceFolder(context: Context): DocumentFile {
            val workspaceFolder =
                DocumentFile.fromTreeUri(context, (SPUtils.getSafRwPath(context)).toUri())!!
            if (workspaceFolder.findFile(DeCompressionUtils.Constants.WORKSPACE_FOLDER) == null)
                workspaceFolder.createDirectory(DeCompressionUtils.Constants.WORKSPACE_FOLDER)!!

            return workspaceFolder.findFile(DeCompressionUtils.Constants.WORKSPACE_FOLDER)!!
        }

        fun cleanWorkspaceFolder(context: Context, deleteAlsoGzFile: Boolean) {
            for (i in getWorkspaceFolder(context).listFiles()) {
                if (deleteAlsoGzFile || !i.name.toString().endsWith("gz"))
                    i.delete()
            }
        }

        fun copyFileToSafFolder(
            context: Context,
            inputFile: Uri,
            outputFilename: String,
            workspaceFolder: DocumentFile
        ): Uri {
            val input: InputStream?
            val output: OutputStream?
            val finalFile: DocumentFile? =
                workspaceFolder.createFile("application/octet-stream", outputFilename)
            try {
                output = context.contentResolver.openOutputStream(finalFile!!.uri)
                input = context.contentResolver.openInputStream(inputFile)
                val buffer = ByteArray(1024)
                var read: Int
                while (input!!.read(buffer).also { read = it } != -1) {
                    output!!.write(buffer, 0, read)
                }
                input.close()
                output!!.flush()
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return finalFile!!.uri
        }
    }

}