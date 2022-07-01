package vegabobo.dsusideloader.dsuhelper

import android.content.Context
import android.net.Uri
import android.util.Log
import vegabobo.dsusideloader.util.FileOperation
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.WorkspaceUtils
import vegabobo.dsusideloader.viewmodel.HomeViewModel
import vegabobo.dsusideloader.viewmodel.InstallationProgress

class PrepareDsu(
    private val context: Context,
    private var gsi: GSI,
    private val homeViewModel: HomeViewModel
) : Runnable {

    private var preparedGsiPath: String = ""
    private var preparedFilesize: Long = -1L

    override fun run() {
        WorkspaceUtils.cleanWorkspaceFolder(context, true)
        prepare()
        gsi.fileSize = preparedFilesize
        gsi.absolutePath = preparedGsiPath
        updateText(InstallationProgress.Steps.FINISHED)
        Deploy(context, gsi).inst()
    }

    private fun getExtension(file: String): String {
        return file.substring(file.lastIndexOf("."))
    }

    fun prepare() {
        val gsiUri = gsi.targetUri
        val filename = getFilenameOnly(gsi.name)
        when (getExtension(gsi.name)) {
            ".xz" -> {
                prepareXz(gsiUri, filename)
            }
            ".img" -> {
                gsi.targetUri = getUriSafe(gsi.targetUri)
                prepareImage(gsiUri, filename)
            }
            ".gz" -> {
                gsi.targetUri = getUriSafe(gsi.targetUri)
                prepareGz(gsiUri, filename)
            }
            ".zip" -> {
                prepareZip(gsiUri)
            }
            else -> {}
        }
    }

    fun copyFile(inputFile: Uri, updateInstallationText: Boolean): Uri {
        if (updateInstallationText) updateText(InstallationProgress.Steps.COPYING_FILE)
        return WorkspaceUtils.copyFileToSafFolder(
            context,
            inputFile,
            FilenameUtils.queryName(context.contentResolver, inputFile)
        )
    }

    fun prepareZip(uri: Uri) {
        preparedFilesize = FilenameUtils.getLengthFromFile(context, uri)
        preparedGsiPath = FilenameUtils.getFilePath(uri, true)
    }

    fun getUriSafe(uri: Uri): Uri {
        if (isPathWrong(uri))
            return copyFile(uri, true)
        return uri
    }

    fun getFilenameOnly(file: String): String {
        return file.split(".")[0]
    }

    fun prepareXz(uri: Uri, outputName: String) {
        val outputFile = "$outputName.img"
        updateText(InstallationProgress.Steps.DECOMPRESSING_XZ)
        val fileOperation = FileOperation(context, uri, outputFile)
        val imgFile = fileOperation.extractXZFile()
        prepareImage(imgFile, outputFile)
    }

    fun prepareImage(uri: Uri, outputName: String) {
        val outputFile = "$outputName.gz"
        updateText(InstallationProgress.Steps.COMPRESSING_TO_GZ)
        preparedFilesize = FilenameUtils.getLengthFromFile(context, uri)
        val fileOperation = FileOperation(context, uri, outputFile)
        val gzFile = fileOperation.compressGzip()
        preparedGsiPath = FilenameUtils.getFilePath(gzFile, true)
    }

    fun prepareGz(uri: Uri, outputName: String) {
        val outputFile = "$outputName.img"
        updateText(InstallationProgress.Steps.DECOMPRESSING_GZIP)
        val fileOperation = FileOperation(context, uri, outputFile)
        val imgFile = fileOperation.decompressGzip()
        preparedFilesize = FilenameUtils.getLengthFromFile(context, imgFile)
        preparedGsiPath = FilenameUtils.getFilePath(uri, true)
    }

    fun isPathWrong(uri: Uri): Boolean {
        return uri.path.toString().contains("msf:")
    }

    private fun updateText(value: Int) {
        homeViewModel.installationProgress.set(value)
    }

}
