package vegabobo.dsusideloader.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vegabobo.dsusideloader.R

class SetupStorageAccess(
    private val ctx: Context
) {

    private lateinit var v: ActivityResultLauncher<Intent>

    init {
        if (!arePermissionsGranted(SPUtils.getSafRwPath(ctx))) {
            setupSAFActivityResult()
            askSafStorageAccess()
        }
    }

    private fun setupSAFActivityResult() {
        v =
            (ctx as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    ctx.contentResolver.takePersistableUriPermission(
                        data?.data!!,
                        takeFlags
                    )
                    SPUtils.writeSafRwPath(ctx, data.data.toString())
                } else {
                    (ctx as Activity).finish()
                }
            }
    }

    private fun arePermissionsGranted(
        folderUri: String
    ): Boolean {
        val foldersUriPermissions = ctx.contentResolver.persistedUriPermissions
        for (folder in foldersUriPermissions) {
            val persistedUriString = folder.uri.toString()
            if (folderUri == persistedUriString) {
                // If folder with granted permissions doesn't exists
                // (eg. user deleted folder, or apk data restored externally from a backup)
                // then, we should ask user to grant permissions to a folder again
                if (!DocumentFile.fromTreeUri(ctx, folder.uri)!!.exists()) {
                    return false
                }

                // check if uri has r/w permission
                if (folder.isWritePermission && folder.isReadPermission) {
                    return true
                }
            } else {
                // if we have permission in some folder we don't need to, permission to it will be revoked
                ctx.revokeUriPermission(
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
        if (!arePermissionsGranted(SPUtils.getSafRwPath(ctx))) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.storage))
                .setMessage(ctx.getString(R.string.storage_info))
                .setPositiveButton(R.string.got_it) { _, _ -> setupSafStorage() }
                .setNegativeButton(R.string.close_app) { _, _ -> (ctx as Activity).finish() }
                .setCancelable(false)
                .show()
        }
    }
}
