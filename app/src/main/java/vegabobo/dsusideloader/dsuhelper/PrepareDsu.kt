package vegabobo.dsusideloader.dsuhelper

import android.content.Context
import android.net.Uri
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.util.FileOperation
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.WorkspaceUtils
import vegabobo.dsusideloader.viewmodel.HomeViewModel

object InstallationSteps {
    const val FINISHED = 0
    const val COPYING_FILE = 1 // -> c.getString(R.string.gz_copy)
    const val DECOMPRESSING_XZ = 2 // -> c.getString(R.string.gz_copy)
    const val COMPRESSING_TO_GZ = 3 // -> c.getString(R.string.compressing_img_to_gzip)
    const val DECOMPRESSING_GZIP = 4 // -> c.getString(R.string.extracting_gzip)
}

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
        updateText(InstallationSteps.FINISHED)
        if(!homeViewModel.installationJob.isCancelled)
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
        if (updateInstallationText) updateText(InstallationSteps.COPYING_FILE)
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
        updateText(InstallationSteps.DECOMPRESSING_XZ)
        val fileOperation = FileOperation(context, uri, outputFile, homeViewModel)
        val imgFile = fileOperation.unpack()
        prepareImage(imgFile, outputFile)
    }

    fun prepareImage(uri: Uri, outputName: String) {
        val outputFile = "$outputName.gz"
        updateText(InstallationSteps.COMPRESSING_TO_GZ)
        preparedFilesize = FilenameUtils.getLengthFromFile(context, uri)
        val fileOperation = FileOperation(context, uri, outputFile, homeViewModel)
        val gzFile = fileOperation.pack()
        preparedGsiPath = FilenameUtils.getFilePath(gzFile, true)
    }

    fun prepareGz(uri: Uri, outputName: String) {
        val outputFile = "$outputName.img"
        updateText(InstallationSteps.DECOMPRESSING_GZIP)
        val fileOperation = FileOperation(context, uri, outputFile, homeViewModel)
        val imgFile = fileOperation.unpack()
        preparedFilesize = FilenameUtils.getLengthFromFile(context, imgFile)
        preparedGsiPath = FilenameUtils.getFilePath(uri, true)
    }

    fun isPathWrong(uri: Uri): Boolean {
        return uri.path.toString().contains("msf:")
    }

    private fun updateText(value: Int) {
        val text = when (value) {
            InstallationSteps.FINISHED -> context.getString(R.string.done)
            InstallationSteps.COPYING_FILE -> context.getString(R.string.copying_file)
            InstallationSteps.DECOMPRESSING_XZ -> context.getString(R.string.extracting_xz)
            InstallationSteps.COMPRESSING_TO_GZ -> context.getString(R.string.compressing_img_to_gzip)
            InstallationSteps.DECOMPRESSING_GZIP -> context.getString(R.string.extracting_gzip)
            else -> {
                context.getString(R.string.error)
            }
        }
        homeViewModel.updateInstallationText(text)
    }

}
