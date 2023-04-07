package vegabobo.dsusideloader.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.compress.utils.IOUtils
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.util.DataStoreUtils
import vegabobo.dsusideloader.util.FilenameUtils

class StorageManager(
    private val appContext: Context,
    private val preferences: DataStore<Preferences>,
) {

    object Constants {
        const val WORKSPACE_FOLDER = "workspace_dsuhelper"
    }

    private var rwPathAllowedByUser: String = ""
    private lateinit var workspaceFolder: DocumentFile

    init {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            DataStoreUtils.readStringPref(preferences, AppPrefs.SAF_PATH, "") {
                arePermissionsGrantedToFolder(it)
            }
        }
    }

    fun arePermissionsGrantedToFolder(path: String): Boolean {
        val foldersUriPermissions = appContext.contentResolver.persistedUriPermissions
        for (folder in foldersUriPermissions) {
            val persistedUriString = folder.uri.toString()
            if (path == persistedUriString) {
                // If folder with granted permissions doesn't exists
                // (eg. user deleted folder, or apk data restored externally from a backup)
                // then, we should ask user to grant permissions to a folder again
                if (!DocumentFile.fromTreeUri(appContext, folder.uri)!!.exists()) {
                    return false
                }

                // check if uri has r/w permission
                if (folder.isWritePermission && folder.isReadPermission) {
                    rwPathAllowedByUser = path
                    return true
                }
            } else {
                // if we have permission in some folder we don't need to, permission to it will be revoked
                appContext.revokeUriPermission(
                    folder.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
            }
        }
        return false
    }

    // Create/obtains a subfolder localized in path selected by user
    // this path will be used to store images that will be utilized by DSU
    @Throws(Exception::class)
    private fun getWorkspaceFolder(): DocumentFile {
        if (this::workspaceFolder.isInitialized && workspaceFolder.canRead()) {
            return workspaceFolder
        }

        if (rwPathAllowedByUser.isEmpty()) {
            throw Exception("Allowed path by user is empty, ask user to allow storage permission again.")
        }

        val writeableDir =
            DocumentFile.fromTreeUri(appContext, rwPathAllowedByUser.toUri())
                ?: throw Exception("Workspace folder cannot be null.")

        workspaceFolder = writeableDir.findFile(Constants.WORKSPACE_FOLDER)
            ?: writeableDir.createDirectory(Constants.WORKSPACE_FOLDER)!!

        return workspaceFolder
    }

    fun cleanWorkspaceFolder(deleteAlsoGzFile: Boolean) {
        for (file in getWorkspaceFolder().listFiles()) {
            if (deleteAlsoGzFile || !file.name.toString().endsWith("gz")) {
                file.delete()
            }
        }
    }

    private fun copyFileToSafFolder(inputFile: Uri): Uri {
        val clone: DocumentFile = createDocumentFile(getFilenameFromUri(inputFile))
        IOUtils.copy(openInputStream(inputFile), openOutputStream(clone.uri))
        return clone.uri
    }

    fun writeStringToFile(content: String, filename: String): String {
        val file = createDocumentFile(filename)
        val outputStream = openOutputStream(file.uri)
        outputStream.write(content.toByteArray())
        outputStream.close()
        return FilenameUtils.getFilePath(file.uri, false).replace("file://", "")
    }

    fun writeStringToExternalFileDir(content: String, filename: String): String {
        val externalFilesDir = appContext.getExternalFilesDir(null)
            ?: throw IOException("externalFilesDir cannot be null.")
        val newFile = File(externalFilesDir.absolutePath + "/$filename")
        if (newFile.exists()) {
            newFile.delete()
        }
        newFile.createNewFile()
        newFile.writeBytes(content.toByteArray())
        return newFile.absolutePath
    }

    fun writeStringToUri(content: String, uri: Uri): String {
        val outputStream = appContext.contentResolver.openOutputStream(uri)!!
        outputStream.write(content.toByteArray())
        outputStream.close()
        return FilenameUtils.getFilePath(uri, false).replace("file://", "")
    }

    fun readFileFromAssets(filename: String): String {
        return appContext.assets.open(filename).bufferedReader()
            .use { it.readText() }
    }

    fun getFilenameFromUri(uri: Uri): String {
        return FilenameUtils.queryName(appContext.contentResolver, uri)
    }

    fun getFilesizeFromUri(uri: Uri): Long {
        return FilenameUtils.getLengthFromFile(appContext, uri)
    }

    fun openInputStream(uri: Uri): InputStream {
        return appContext.contentResolver.openInputStream(uri)!!
    }

    fun openOutputStream(uri: Uri): OutputStream {
        return appContext.contentResolver.openOutputStream(uri)!!
    }

    fun createDocumentFile(filename: String): DocumentFile {
        return getWorkspaceFolder().createFile("application/octet-stream", filename)!!
    }

    fun getUriSafe(uri: Uri): Uri {
        if (isPathWrong(uri)) {
            return copyFileToSafFolder(uri)
        }
        return uri
    }

    private fun isPathWrong(uri: Uri): Boolean {
        return uri.path.toString().contains("msf:")
    }
}
