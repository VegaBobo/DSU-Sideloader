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
import java.math.BigInteger
import vegabobo.dsusideloader.LogsActivity
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.RunOnAdbActivity
import vegabobo.dsusideloader.checks.OperationMode
import vegabobo.dsusideloader.util.DeCompressionUtils
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils
import vegabobo.dsusideloader.util.WorkspaceFilesUtils

class PrepareDsu(
    private val ctx: Context,
    private val uri: Uri,
    private val gsiDsuObject: GsiDsuObject?
) : Runnable {

    private lateinit var dialog: AlertDialog
    var cleanWorkspace = true

    override fun run() {
        val builder = MaterialAlertDialogBuilder(ctx)
        (ctx as Activity).runOnUiThread {
            builder.setCancelable(false)
            builder.setView(R.layout.progress)
            dialog = builder.create()
            dialog.show()
        }

        val file = FilenameUtils.queryName(ctx.contentResolver, uri)

        val dsu: GsiDsuObject? = when (file.substringAfterLast(".")) {
            "xz" -> {
                transformFile2Gzip(
                    uri,
                    DeCompressionUtils.Constants.XZ_TO_IMG,
                    gsiDsuObject
                )
            }
            "img" -> {
                transformFile2Gzip(
                    uri,
                    DeCompressionUtils.Constants.IMG_TO_GZ,
                    gsiDsuObject
                )
            }
            "gz" -> {
                var gzUri = uri
                if (uri.path.toString().contains("msf:")) {
                    updateText(ctx.getString(R.string.gz_copy))
                    gzUri = WorkspaceFilesUtils.copyFileToSafFolder(
                        ctx,
                        uri,
                        file,
                        WorkspaceFilesUtils.getWorkspaceFolder(ctx)
                    )
                }
                if (gsiDsuObject!!.fileSize != -1L) {
                    gsiDsuObject.absolutePath =
                        FilenameUtils.getFilePath(gzUri, true)
                }

                transformFile2Gzip(
                    gzUri,
                    DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT,
                    gsiDsuObject
                )
            }
            "zip" -> {
                val dsuPackageUri = if ("msf:" in uri.path.toString()) {
                    updateText(ctx.getString(R.string.copying_file))
                    WorkspaceFilesUtils.copyFileToSafFolder(
                        ctx,
                        uri,
                        "dsu.zip",
                        WorkspaceFilesUtils.getWorkspaceFolder(ctx)
                    )
                } else {
                    uri
                }
                cleanWorkspace = false
                val filePath = FilenameUtils.getFilePath(dsuPackageUri, true)

                // workaround for java.net.URISyntaxException: Illegal character in path at index
                // com.android.dynsystem.InstallationAsyncTask.verifyAndPrepare(InstallationAsyncTask.java:273)
                if (filePath.contains(" ")) {
                    gsiDsuObject!!.absolutePath = filePath.replace(" ", "%20")
                } else {
                    gsiDsuObject!!.absolutePath = filePath
                }

                gsiDsuObject
            }
            else -> gsiDsuObject
        }

        ctx.runOnUiThread {
            dialog.dismiss()
        }

        if (cleanWorkspace) {
            WorkspaceFilesUtils.cleanWorkspaceFolder(ctx, false)
        }

        when (OperationMode.getOperationMode()) {
            OperationMode.Constants.ROOT_MAGISK, OperationMode.Constants.OTHER_ROOT_SOLUTION -> {
                val dsuCommand = DSUCommand(dsu!!, ctx, true)

                if (SPUtils.isDebugModeEnabled(ctx)) {
                    ctx.startActivity(
                        Intent(ctx, LogsActivity::class.java).putExtra(
                            "script",
                            dsuCommand.getInstallScript()
                        ).putExtra(
                            "installation_info",
                            dsuCommand.installationInfoAsString()
                        )
                    )
                } else {
                    showFinishedDialog()
                    Shell.cmd(dsuCommand.getInstallScript()).exec()
                }
            }
            OperationMode.Constants.UNROOTED -> {
                val dsuCommand = DSUCommand(dsu!!, ctx, false)
                showAdbCommandToDeployGSI(dsuCommand.writeInstallScript())
            }
        }
    }

    private fun transformFile2Gzip(uri: Uri, mode: Int, gsiDsuObject: GsiDsuObject?): GsiDsuObject? {
        val workspaceFolder = WorkspaceFilesUtils.getWorkspaceFolder(ctx)
        val outputFilename = FilenameUtils.queryName(ctx.contentResolver, uri)

        return when (mode) {
            DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT -> {
                updateText(ctx.getString(R.string.almost_ready))
                val gsiSize = gsiDsuObject!!.fileSize
                val gsiAbsPath = gsiDsuObject.absolutePath
                if (gsiSize == -1L && gsiAbsPath == "") {
                    transformFile2Gzip(
                        uri,
                        DeCompressionUtils.Constants.GZ_TO_IMG,
                        gsiDsuObject
                    )
                } else {
                    gsiDsuObject
                }
            }
            DeCompressionUtils.Constants.XZ_TO_IMG -> {
                updateText(ctx.getString(R.string.extracting_xz))
                val filenameWithoutExtension =
                    outputFilename.substringBeforeLast(".")

                val uriFromIMGFile = DeCompressionUtils(
                    ctx,
                    uri,
                    filenameWithoutExtension,
                    workspaceFolder
                ).extractXZFile()!!

                transformFile2Gzip(
                    uriFromIMGFile,
                    DeCompressionUtils.Constants.IMG_TO_GZ,
                    gsiDsuObject
                )
            }
            DeCompressionUtils.Constants.IMG_TO_GZ -> {
                updateText(ctx.getString(R.string.compressing_img_to_gzip))
                val uriFromGZFile = DeCompressionUtils(
                    ctx,
                    uri,
                    "$outputFilename.gz",
                    workspaceFolder
                ).compressGzip()
                val absolutePath = FilenameUtils.getFilePath(uriFromGZFile!!, true)
                val bytesFromImageFile =
                    DocumentFile.fromSingleUri(ctx, uri)!!.length()
                gsiDsuObject!!.fileSize = bytesFromImageFile
                gsiDsuObject.absolutePath = absolutePath
                transformFile2Gzip(
                    uriFromGZFile,
                    DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT,
                    gsiDsuObject
                )
            }
            DeCompressionUtils.Constants.GZ_TO_IMG -> {
                val absolutePath = FilenameUtils.getFilePath(uri, true)
                gsiDsuObject!!.absolutePath = absolutePath

                val bytesFromImageFile = getImageSize(
                    uri,
                    workspaceFolder,
                    outputFilename
                )
                gsiDsuObject.fileSize = bytesFromImageFile

                transformFile2Gzip(uri, DeCompressionUtils.Constants.GZ_TO_GSI_OBJECT, gsiDsuObject)
            }
            else -> null
        }
    }

    private fun getImageSize(uri: Uri, workspace: DocumentFile, output: String): Long {
        val fileSize = DocumentFile.fromSingleUri(ctx, uri)!!.length()
        val three_gb = Int.MAX_VALUE.toLong() * 1.5 // 2^32 * 0.75

        // If the .gz is smaller than 3gb, then try returning the image size
        // by reading the lasts four bytes.
        if (fileSize < three_gb) {
            val inputStream = ctx.contentResolver.openInputStream(uri)!!
            inputStream.skip(fileSize - 4)
            val bytes = ByteArray(4)
            inputStream.read(bytes)
            bytes.reverse() // Little endian -> Big endian
            val imageSize = BigInteger(1, bytes).toLong()
            // If the image size is LOWER than the compressed file, then
            // the image size must be wrong.
            if (imageSize > fileSize) {
                return imageSize
            }
        }
        // If the .gz is bigger than 3gb or the fast-way returns a
        // wrong value, we need to decompress the file and calculate
        // the size. SLOWWWWWWWWWWWWWW
        updateText(ctx.getString(R.string.extracting_gzip))
        val filenameWithoutExtension = output.substringBeforeLast(".")

        val decompressedImage = DeCompressionUtils(
            ctx,
            uri,
            filenameWithoutExtension,
            workspace
        ).decompressGzip()!!

        val image = DocumentFile.fromSingleUri(ctx, decompressedImage)!!
        return image.length()
    }

    private fun updateText(text: String) {
        (ctx as Activity).runOnUiThread {
            val loadingMsg = dialog.findViewById<TextView>(R.id.tv_loadingtext)
            loadingMsg!!.text = text
        }
    }

    private fun showFinishedDialog() {
        (ctx as Activity).runOnUiThread {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.done))
                .setMessage(ctx.getString(R.string.process_finished))
                .setPositiveButton(ctx.getString(R.string.close_app)) { _, _ -> ctx.finish() }
                .setCancelable(false)
                .show()
        }
    }

    private fun showAdbCommandToDeployGSI(cmdline: String) {
        (ctx as Activity).runOnUiThread {
            val myIntent = Intent(ctx, RunOnAdbActivity::class.java)
            myIntent.putExtra("cmdline", "adb shell sh $cmdline")
            ctx.startActivity(myIntent)
        }
    }
}
