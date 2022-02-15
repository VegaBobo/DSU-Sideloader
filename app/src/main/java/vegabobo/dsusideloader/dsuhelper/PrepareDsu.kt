package vegabobo.dsusideloader.dsuhelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.LogsActivity
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.RunOnAdbActivity
import vegabobo.dsusideloader.checks.OperationMode
import vegabobo.dsusideloader.util.DeCompressionUtils
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils
import vegabobo.dsusideloader.util.WorkspaceFilesUtils

class PrepareDsu(
    private val c: Context,
    private val uri: Uri,
    private val gsiDsuObject: GsiDsuObject?
) : Runnable {

    private lateinit var dialog: AlertDialog
    var cleanWorkspace = true

    override fun run() {

        val builder = MaterialAlertDialogBuilder(c)
        (c as Activity).runOnUiThread {
            builder.setCancelable(false)
            builder.setView(R.layout.progress)
            dialog = builder.create()
            dialog.show()
        }

        val file = FilenameUtils.queryName(c.contentResolver, uri)

        val dsu: GsiDsuObject? = when (file.substring(file.lastIndexOf("."))) {
            ".xz" -> {
                transformFile2Gzip(uri, DeCompressionUtils.Constants.XZ_TO_IMG, gsiDsuObject)
            }
            ".img" -> {
                transformFile2Gzip(uri, DeCompressionUtils.Constants.IMG_TO_GZ, gsiDsuObject)
            }
            ".gz" -> {
                var gzUri = uri
                if (uri.path.toString().contains("msf:")) {
                    updateText(c.getString(R.string.gz_copy))
                    gzUri = WorkspaceFilesUtils.copyFileToSafFolder(
                        c,
                        uri,
                        file,
                        WorkspaceFilesUtils.getWorkspaceFolder(c)
                    )
                }
                if (gsiDsuObject!!.fileSize != -1L)
                    gsiDsuObject.absolutePath =
                        FilenameUtils.getFilePath(gzUri, true)
                transformFile2Gzip(
                    gzUri,
                    DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT,
                    gsiDsuObject
                )
            }
            ".zip" -> {
                val dsuPackageUri = if (uri.path.toString().contains("msf:")) {
                    updateText(c.getString(R.string.copying_file))
                    WorkspaceFilesUtils.copyFileToSafFolder(
                        c,
                        uri,
                        "dsu.zip",
                        WorkspaceFilesUtils.getWorkspaceFolder(c)
                    )
                } else {
                    uri
                }
                cleanWorkspace = false
                gsiDsuObject!!.absolutePath = FilenameUtils.getFilePath(dsuPackageUri, true)
                gsiDsuObject
            }
            else ->
                gsiDsuObject
        }

        c.runOnUiThread {
            dialog.dismiss()
        }

        if (cleanWorkspace)
            WorkspaceFilesUtils.cleanWorkspaceFolder(c, false)

        when (OperationMode.getOperationMode()) {

            OperationMode.Constants.ROOT_MAGISK, OperationMode.Constants.OTHER_ROOT_SOLUTION -> {

                if (SPUtils.isDebugModeEnabled(c)) {
                    Shell.su("logcat -c").submit()
                    val intent = Intent(c, LogsActivity::class.java)
                    intent.putExtra("dsu", dsu)
                    c.startActivity(intent)
                } else {
                    showFinishedDialog()
                    RootDSUDeployer(dsu!!)
                }

            }

            OperationMode.Constants.UNROOTED -> {
                val p = GenAdbDsuInstallationScript(dsu!!, c).generateScriptFile()
                showAdbCommandToDeployGSI(p)
            }

        }
    }


    private fun transformFile2Gzip(uri: Uri, op: Int, gsiDsuObject: GsiDsuObject?): GsiDsuObject? {

        val workspaceFolder = WorkspaceFilesUtils.getWorkspaceFolder(c)
        val outputFilename = FilenameUtils.queryName(c.contentResolver, uri)

        return when (op) {
            DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT -> {
                updateText(c.getString(R.string.almost_ready))
                if (gsiDsuObject!!.fileSize == -1L && gsiDsuObject.absolutePath == "") {
                    transformFile2Gzip(uri, DeCompressionUtils.Constants.GZ_TO_IMG, gsiDsuObject)
                } else {
                    return gsiDsuObject
                }
            }
            DeCompressionUtils.Constants.XZ_TO_IMG -> {
                updateText(c.getString(R.string.extracting_xz))
                val filenameWithoutExtension =
                    outputFilename.substring(0, outputFilename.length - 3)
                transformFile2Gzip(
                    DeCompressionUtils(
                        c,
                        uri,
                        filenameWithoutExtension,
                        workspaceFolder
                    ).extractXZFile()!!, DeCompressionUtils.Constants.IMG_TO_GZ, gsiDsuObject
                )
            }
            DeCompressionUtils.Constants.IMG_TO_GZ -> {
                updateText(c.getString(R.string.compressing_img_to_gzip))
                val uriFromGZFile = DeCompressionUtils(
                    c,
                    uri,
                    "${outputFilename}.gz",
                    workspaceFolder
                ).compressGzip()
                val absolutePath = FilenameUtils.getFilePath(uriFromGZFile!!, true)
                val bytesFromImageFile =
                    DocumentFile.fromSingleUri(c, uri)!!.length()
                gsiDsuObject!!.fileSize = bytesFromImageFile
                gsiDsuObject.absolutePath = absolutePath
                transformFile2Gzip(
                    uriFromGZFile,
                    DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT,
                    gsiDsuObject
                )
            }
            DeCompressionUtils.Constants.GZ_TO_IMG -> {
                updateText(c.getString(R.string.extracting_gzip))
                val filenameWithoutExtension =
                    outputFilename.substring(0, outputFilename.length - 3)
                val p = DeCompressionUtils(
                    c,
                    uri,
                    filenameWithoutExtension,
                    workspaceFolder
                ).decompressGzip()
                val absolutePath = FilenameUtils.getFilePath(uri, true)
                gsiDsuObject!!.absolutePath = absolutePath

                val bytesFromImageFile =
                    DocumentFile.fromSingleUri(c, p!!)!!.length()
                gsiDsuObject.fileSize = bytesFromImageFile

                transformFile2Gzip(uri, DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT, gsiDsuObject)
            }
            else -> null
        }

    }

    private fun updateText(text: String) {
        (c as Activity).runOnUiThread {
            val loadingMsg = dialog.findViewById<TextView>(R.id.tv_loadingtext)
            loadingMsg!!.text = text
        }
    }

    private fun showFinishedDialog() {
        (c as Activity).runOnUiThread {
            MaterialAlertDialogBuilder(c)
                .setTitle(c.getString(R.string.done))
                .setMessage(c.getString(R.string.process_finished))
                .setPositiveButton(c.getString(R.string.close_app)) { _, _ -> c.finish() }
                .setCancelable(false)
                .show()
        }
    }

    private fun showAdbCommandToDeployGSI(cmdline: String) {
        (c as Activity).runOnUiThread {
            val myIntent = Intent(c, RunOnAdbActivity::class.java)
            myIntent.putExtra("cmdline", "adb shell sh $cmdline")
            c.startActivity(myIntent)
        }
    }


}