package vegabobo.dsusideloader.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

    private fun setupSafStorage() {
        val rwPath = SPUtils.getSafRwPath(c)
        if (rwPath.isEmpty() || arePermissionsGranted(rwPath)) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            v.launch(intent)
        }
    }

    private fun setupSAFActivityResult() {
        v =
            (c as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        uriString: String
    ): Boolean {
        val list = c.contentResolver.persistedUriPermissions
        for (i in list.indices) {
            val persistedUriString = list[i].uri.toString()
            if (persistedUriString == uriString && list[i].isWritePermission && list[i].isReadPermission) {
                return true
            }
        }
        return false
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