package vegabobo.dsusideloader.preparation

import android.net.Uri
import kotlinx.coroutines.CompletableJob
import vegabobo.dsusideloader.installation.Deploy
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.util.FilenameUtils

class PrepareFile(
    private val storageManager: StorageManager,
    private var gsi: TargetGSI,
    private val installationJob: CompletableJob,
    private val onInstallationStepChange: (Int) -> Unit,
    private val onProgressChange: (Float) -> Unit
) : () -> Unit {

    private var preparedGsiPath: String = ""
    private var preparedFilesize: Long = -1L

    override fun invoke() {
        storageManager.cleanWorkspaceFolder(true)
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
        }
        gsi.fileSize = preparedFilesize
        gsi.absolutePath = preparedGsiPath
        updateText(InstallationSteps.FINISHED)
        if (!installationJob.isCancelled)
            Deploy(storageManager, gsi).startInstallation()
    }

    private fun getExtension(file: String): String {
        return file.substring(file.lastIndexOf("."))
    }

    private fun copyFile(inputFile: Uri): Uri {
        updateText(InstallationSteps.COPYING_FILE)
        return storageManager.copyFileToSafFolder(inputFile)
    }

    private fun prepareZip(uri: Uri) {
        preparedFilesize = storageManager.getFilesizeFromUri(uri)
        preparedGsiPath = FilenameUtils.getFilePath(uri, true)
    }

    private fun getUriSafe(uri: Uri): Uri {
        if (isPathWrong(uri))
            return copyFile(uri)
        return uri
    }

    private fun getFilenameOnly(file: String): String {
        return file.split(".")[0]
    }

    private fun prepareXz(uri: Uri, outputName: String) {
        val outputFile = "$outputName.img"
        updateText(InstallationSteps.DECOMPRESSING_XZ)
        val fileOperation =
            UnPack(storageManager, uri, outputFile, installationJob, onProgressChange)
        val imgFile = fileOperation.unpack()
        prepareImage(imgFile, outputFile)
    }

    private fun prepareImage(uri: Uri, outputName: String) {
        val outputFile = "$outputName.gz"
        updateText(InstallationSteps.COMPRESSING_TO_GZ)
        preparedFilesize = storageManager.getFilesizeFromUri(uri)
        val fileOperation =
            UnPack(storageManager, uri, outputFile, installationJob, onProgressChange)
        val gzFile = fileOperation.pack()
        preparedGsiPath = FilenameUtils.getFilePath(gzFile, true)
    }

    private fun prepareGz(uri: Uri, outputName: String) {
        val outputFile = "$outputName.img"
        updateText(InstallationSteps.DECOMPRESSING_GZIP)
        val fileOperation =
            UnPack(storageManager, uri, outputFile, installationJob, onProgressChange)
        val imgFile = fileOperation.unpack()
        preparedFilesize = storageManager.getFilesizeFromUri(imgFile)
        preparedGsiPath = FilenameUtils.getFilePath(uri, true)
    }

    private fun isPathWrong(uri: Uri): Boolean {
        return uri.path.toString().contains("msf:")
    }

    private fun updateText(value: Int) {
        onInstallationStepChange(value)
    }

}
