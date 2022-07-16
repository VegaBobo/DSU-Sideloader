package vegabobo.dsusideloader.preparation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import vegabobo.dsusideloader.installation.GenInstallation
import vegabobo.dsusideloader.util.FilenameUtils
import java.io.InputStream
import java.io.OutputStream

class StorageManager(
    private val context: Context
) {

    private var readWritePath = ""

    fun arePermissionsGrantedToFolder(
        path: String
    ): Boolean {
        val foldersUriPermissions = context.contentResolver.persistedUriPermissions
        for (folder in foldersUriPermissions) {
            val persistedUriString = folder.uri.toString()
            if (path == persistedUriString) {

                // If folder with granted permissions doesn't exists
                // (eg. user deleted folder, or apk data restored externally from a backup)
                // then, we should ask user to grant permissions to a folder again
                if (!DocumentFile.fromTreeUri(context, folder.uri)!!.exists())
                    return false

                // check if uri has r/w permission
                if (folder.isWritePermission && folder.isReadPermission) {
                    this.readWritePath = path
                    return true
                }

            } else {
                // if we have permission in some folder we don't need to, permission to it will be revoked
                context.revokeUriPermission(
                    folder.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
        return false
    }

    private fun getWorkspaceFolder(): DocumentFile {
        val workspaceFolder =
            DocumentFile.fromTreeUri(context, readWritePath.toUri())!!
        if (workspaceFolder.findFile(UnPack.Constants.WORKSPACE_FOLDER) == null)
            workspaceFolder.createDirectory(UnPack.Constants.WORKSPACE_FOLDER)!!
        return workspaceFolder.findFile(UnPack.Constants.WORKSPACE_FOLDER)!!
    }

    fun cleanWorkspaceFolder(deleteAlsoGzFile: Boolean) {
        for (i in getWorkspaceFolder().listFiles()) {
            if (deleteAlsoGzFile || !i.name.toString().endsWith("gz"))
                i.delete()
        }
    }

    fun copyFileToSafFolder(
        inputFile: Uri
    ): Uri {
        val input: InputStream?
        val output: OutputStream?
        val finalFile: DocumentFile? =
            getWorkspaceFolder()
                .createFile("application/octet-stream", getFilenameFromUri(inputFile))
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

    fun writeToFile(content: String): String {
        val d = createDocumentFile(GenInstallation.Constants.INSTALL_SCRIPT_FILENAME)
        val outputStream = context.contentResolver.openOutputStream(d.uri)
        val strToBytes = content.toByteArray()
        outputStream!!.write(strToBytes)
        outputStream.close()
        return FilenameUtils.getFilePath(d.uri, false).replace("file://", "")
    }

    fun readFileFromAssets(filename: String): String {
        return context.assets.open(filename).bufferedReader()
            .use { it.readText() }
    }

    fun getFilenameFromUri(uri: Uri): String {
        return FilenameUtils.queryName(context.contentResolver, uri)
    }

    fun getFilesizeFromUri(uri: Uri): Long {
        return FilenameUtils.getLengthFromFile(context, uri)
    }

    fun openInputStream(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)!!
    }

    fun openOutputStream(uri: Uri): OutputStream {
        return context.contentResolver.openOutputStream(uri)!!
    }

    fun createDocumentFile(filename: String): DocumentFile {
        return getWorkspaceFolder().createFile("application/octet-stream", filename)!!
    }

}
