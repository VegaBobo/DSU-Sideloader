package vegabobo.dsusideloader.preparation

import android.net.Uri
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.util.FilenameUtils

data class PreparedFile(
    val archiveUri: Uri = Uri.EMPTY,
    val extractedFileSize: Long = GSI.GSIConstants.DEFAULT_FILE_SIZE,
)

class Preparation(
    private val storageManager: StorageManager,
    private val session: InstallationSession,
    private val onInstallationStepChange: (PreparationSteps) -> Unit,
    private val onProgressChange: (Float) -> Unit,
    private val onPreparationSuccess: (InstallationSession) -> Unit,
) {

    init {
        storageManager.cleanWorkspaceFolder(true)
        val targetFile = session.gsi.uri
        val preparedFile =
            when (session.getTargetFileExtension()) {
                ".xz" -> prepareXz(targetFile)
                ".img" -> prepareImage(targetFile)
                ".gz" -> prepareGz(targetFile)
                ".zip" -> prepareZip(targetFile)
                else -> PreparedFile()
            }

        if (preparedFile.extractedFileSize != GSI.GSIConstants.DEFAULT_FILE_SIZE)
            session.gsi.fileSize = preparedFile.extractedFileSize

        session.gsi.uri = preparedFile.archiveUri
        session.gsi.absolutePath = FilenameUtils.getFilePath(preparedFile.archiveUri, true)
        updateInstallationStep(PreparationSteps.FINISHED)

        if (!session.job.isCancelled)
            onPreparationSuccess(session)
    }

    private fun prepareZip(inputZipFile: Uri): PreparedFile {
        val uri = getSafeUri(inputZipFile)
        return PreparedFile(uri)
    }

    private fun prepareXz(inputXzFile: Uri): PreparedFile {
        val outputFile = "${session.getTargetFilename()}.img"
        updateInstallationStep(PreparationSteps.DECOMPRESSING_XZ)
        val extractedFileUri =
            FileUnPacker(
                storageManager,
                inputXzFile,
                outputFile,
                session.job,
                onProgressChange
            ).unpack()
        return prepareImage(extractedFileUri)
    }

    private fun prepareImage(inputImageFile: Uri): PreparedFile {
        val outputFile = "${session.selectedFilename}.gz"
        updateInstallationStep(PreparationSteps.COMPRESSING_TO_GZ)
        val finalGzFile =
            FileUnPacker(
                storageManager,
                inputImageFile,
                outputFile,
                session.job,
                onProgressChange
            ).pack()
        val imageFileSize = storageManager.getFilesizeFromUri(inputImageFile)
        return PreparedFile(finalGzFile, imageFileSize)
    }

    private fun prepareGz(inputGzFile: Uri): PreparedFile {
        val uri = getSafeUri(inputGzFile)
        if (session.isCustomFileSize())
            return PreparedFile(uri)
        val outputFile = "$uri.img"
        updateInstallationStep(PreparationSteps.DECOMPRESSING_GZIP)
        val imgFile =
            FileUnPacker(storageManager, uri, outputFile, session.job, onProgressChange).unpack()
        val imageFileSize = storageManager.getFilesizeFromUri(imgFile)
        return PreparedFile(inputGzFile, imageFileSize)
    }

    private fun updateInstallationStep(value: PreparationSteps) {
        onInstallationStepChange(value)
    }

    private fun getSafeUri(uri: Uri): Uri {
        updateInstallationStep(PreparationSteps.COPYING_FILE)
        return storageManager.getUriSafe(uri)
    }

}
