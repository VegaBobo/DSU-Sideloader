package vegabobo.dsusideloader.util

import android.content.Context
import android.content.Intent
import androidx.documentfile.provider.DocumentFile

class SetupStorageAccess {

    companion object {

        fun arePermissionsGranted(context: Context): Boolean {
            val folderFromSharedPreferences = SPUtils.getSafRwPath(context)
            return arePermissionsGrantedToFolder(context, folderFromSharedPreferences)
        }

        private fun arePermissionsGrantedToFolder(
            context: Context,
            folderUri: String
        ): Boolean {
            val foldersUriPermissions = context.contentResolver.persistedUriPermissions
            for (folder in foldersUriPermissions) {
                val persistedUriString = folder.uri.toString()
                if (folderUri == persistedUriString) {

                    // If folder with granted permissions doesn't exists
                    // (eg. user deleted folder, or apk data restored externally from a backup)
                    // then, we should ask user to grant permissions to a folder again
                    if (!DocumentFile.fromTreeUri(context, folder.uri)!!.exists())
                        return false

                    // check if uri has r/w permission
                    if (folder.isWritePermission && folder.isReadPermission)
                        return true

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

    }


}