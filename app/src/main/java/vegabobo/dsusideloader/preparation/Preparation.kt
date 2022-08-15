package vegabobo.dsusideloader.preparation

import android.net.Uri
import kotlinx.coroutines.Job
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.model.DSUConstants
import vegabobo.dsusideloader.model.DSUInstallation

interface IPreparation {
    fun onInstallationStepChange(step: InstallationSteps)
    fun onProgressChange(progress: Float)
    fun onPreparationSuccess(preparedDSUInstallation: DSUInstallation)
}

abstract class Preparation(
    private val storageManager: StorageManager,
    private val userSelectedFileUri: Uri,
    private val userSelectedImageSize: Long = DSUConstants.DEFAULT_IMAGE_SIZE,
    private val job: Job,
) : IPreparation {

    init {
        prepareForDSU()
    }

    private fun prepareRooted() {
        val source: DSUInstallation = when (getExtension(userSelectedFileUri)) {
            "img" -> {
                DSUInstallation.SingleSystemImage(
                    userSelectedFileUri, getFileSize(userSelectedFileUri)
                )
            }
            "xz", "gz", "gzip" -> {
                val result = extractFile(userSelectedFileUri)
                DSUInstallation.SingleSystemImage(result.first, result.second)
            }
            "zip" -> {
                DSUInstallation.DsuPackage(userSelectedFileUri)
            }
            else -> {
                throw Exception("Unsupported filetype")
            }
        }
        onPreparationSuccess(source)
    }

    private fun prepareForDSU() {
        storageManager.cleanWorkspaceFolder(true)
        val fileExtension = getExtension(userSelectedFileUri)
        val preparedFilePair =
            when (fileExtension) {
                "xz" -> prepareXz(userSelectedFileUri)
                "img" -> prepareImage(userSelectedFileUri)
                "gz" -> prepareGz(userSelectedFileUri)
                "zip" -> prepareZip(userSelectedFileUri)
                else -> throw Exception("Unsupported filetype")
            }

        val source: DSUInstallation

        val preparedUri = preparedFilePair.first
        val preparedFileSize = preparedFilePair.second

        source = if (fileExtension == "zip")
            DSUInstallation.DsuPackage(preparedUri)
        else
            DSUInstallation.SingleSystemImage(preparedUri, preparedFileSize)

        updateInstallationStep(InstallationSteps.WAITING_USER_CONFIRMATION)

        if (!job.isCancelled)
            onPreparationSuccess(source)
    }

    private fun prepareZip(inputZipFile: Uri): Pair<Uri, Long> {
        val uri = getSafeUri(inputZipFile)
        return Pair(uri, -1)
    }

    private fun prepareXz(inputXzFile: Uri): Pair<Uri, Long> {
        val outputFile = "${getFileName(inputXzFile)}.img"
        updateInstallationStep(InstallationSteps.DECOMPRESSING_XZ)
        return FileUnPacker(
            storageManager,
            inputXzFile,
            outputFile,
            job,
            this::onProgressChange
        ).unpack()
    }

    private fun prepareImage(inputImageFile: Uri): Pair<Uri, Long> {
        val outputFile = "${getFileName(inputImageFile)}.gz"
        updateInstallationStep(InstallationSteps.COMPRESSING_TO_GZ)
        val pair = FileUnPacker(
            storageManager,
            inputImageFile,
            outputFile,
            job,
            this::onProgressChange
        ).pack()
        return Pair(pair.first, getFileSize(inputImageFile))
    }

    private fun prepareGz(inputGzFile: Uri): Pair<Uri, Long> {
        val uri = getSafeUri(inputGzFile)
        if (userSelectedImageSize != DSUConstants.DEFAULT_IMAGE_SIZE)
            return Pair(uri, -1)
        val outputFile = "${getFileName(uri)}.img"
        updateInstallationStep(InstallationSteps.DECOMPRESSING_GZIP)
        val pair =
            FileUnPacker(storageManager, uri, outputFile, job, this::onProgressChange).unpack()
        return Pair(inputGzFile, pair.second)
    }

    private fun extractFile(uri: Uri): Pair<Uri, Long> {
        return extractFile(uri, "system")
    }

    private fun extractFile(uri: Uri, partitionName: String): Pair<Uri, Long> {
        updateInstallationStep(InstallationSteps.EXTRACTING_FILE)
        return FileUnPacker(
            storageManager,
            uri,
            "${partitionName}.img",
            job,
            this::onProgressChange
        ).unpack()
    }

    private fun updateInstallationStep(value: InstallationSteps) {
        onInstallationStepChange(value)
    }

    private fun getSafeUri(uri: Uri): Uri {
        updateInstallationStep(InstallationSteps.COPYING_FILE)
        return storageManager.getUriSafe(uri)
    }

    private fun getFileName(uri: Uri): String {
        return storageManager.getFilenameFromUri(uri)
            .substringBeforeLast(".")
    }

    private fun getExtension(uri: Uri): String {
        return storageManager.getFilenameFromUri(uri)
            .substringAfterLast(".", "")
    }

    private fun getFileSize(uri: Uri): Long {
        return storageManager.getFilesizeFromUri(uri)
    }
}
