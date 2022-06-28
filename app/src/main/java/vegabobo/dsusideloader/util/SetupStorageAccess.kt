package vegabobo.dsusideloader.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vegabobo.dsusideloader.R

class SetupStorageAccess(
    private val c: Context
) {

    private lateinit var v: ActivityResultLauncher<Intent>

    init {
        if (!arePermissionsGranted(SPUtils.getSafRwPath(c))) {
            setupSAFActivityResult()
            askSafStorageAccess()
        }
    }

    private fun setupSAFActivityResult() {
        v =
            (c as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    c.contentResolver.takePersistableUriPermission(
                        data?.data!!,
                        takeFlags
                    )
                    SPUtils.writeSafRwPath(c, data.data.toString())
                }else{
                    (c as Activity).finish()
                }
            }
    }

    private fun arePermissionsGranted(
        folderUri: String
    ): Boolean {
        val foldersUriPermissions = c.contentResolver.persistedUriPermissions
        for (folder in foldersUriPermissions) {
            val persistedUriString = folder.uri.toString()
            if (folderUri == persistedUriString) {

                // If folder with granted permissions doesn't exists
                // (eg. user deleted folder, or apk data restored externally from a backup)
                // then, we should ask user to grant permissions to a folder again
                if (!DocumentFile.fromTreeUri(c, folder.uri)!!.exists())
                    return false

                // check if uri has r/w permission
                if (folder.isWritePermission && folder.isReadPermission)
                    return true

            } else {
                // if we have permission in some folder we don't need to, permission to it will be revoked
                c.revokeUriPermission(
                    folder.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
        return false
    }

    private fun setupSafStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        v.launch(intent)
    }

    private fun askSafStorageAccess() {
        if (!arePermissionsGranted(SPUtils.getSafRwPath(c))) {
            MaterialAlertDialogBuilder(c)
                .setTitle(c.getString(R.string.storage))
                .setMessage(c.getString(R.string.storage_info))
                .setPositiveButton(R.string.got_it) { _, _ -> setupSafStorage() }
                .setNegativeButton(R.string.close_app) { _, _ -> (c as Activity).finish() }
                .setCancelable(false)
                .show()
        }
    }

}