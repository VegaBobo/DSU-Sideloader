package vegabobo.dsusideloader.preparation

import android.net.Uri
import java.math.BigInteger
import kotlinx.coroutines.Job
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.model.DSUConstants
import vegabobo.dsusideloader.model.DSUInstallationSource
import vegabobo.dsusideloader.model.Session

class Preparation(
    private val storageManager: StorageManager,
    private val session: Session,
    private val job: Job,
    private val onStepUpdate: (step: InstallationStep) -> Unit,
    private val onPreparationProgressUpdate: (progress: Float) -> Unit,
    private val onCanceled: () -> Unit,
    private val onPreparationFinished: (preparedDSU: DSUInstallationSource) -> Unit,
) : () -> Unit {

    private val userSelectedImageSize = session.userSelection.userSelectedImageSize
    private val userSelectedFileUri = session.userSelection.selectedFileUri

    override fun invoke() {
        if (session.preferences.useBuiltinInstaller && session.isRoot()) {
            prepareRooted()
            return
        }
        prepareForDSU()
    }

    private fun prepareRooted() {
        val source: DSUInstallationSource = when (getExtension(userSelectedFileUri)) {
            "img" -> {
                DSUInstallationSource.SingleSystemImage(
                    userSelectedFileUri,
                    storageManager.getFilesizeFromUri(userSelectedFileUri),
                )
            }

            "xz", "gz", "gzip" -> {
                val result = extractFile(userSelectedFileUri)
                DSUInstallationSource.SingleSystemImage(result.first, result.second)
            }

            "zip" -> {
                DSUInstallationSource.DsuPackage(userSelectedFileUri)
            }

            else -> {
                throw Exception("Unsupported filetype")
            }
        }
        if (!job.isCancelled) {
            onPreparationFinished(source)
        } else {
            onCanceled()
        }
    }

    private fun prepareForDSU() {
        storageManager.cleanWorkspaceFolder(true)
        val fileExtension = getExtension(userSelectedFileUri)
        val preparedFilePair =
            when (fileExtension) {
                "xz" -> prepareXz(userSelectedFileUri)
                "img" -> prepareImage(userSelectedFileUri)
                "gz", "gzip" -> prepareGz(userSelectedFileUri)
                "zip" -> prepareZip(userSelectedFileUri)
                else -> throw Exception("Unsupported filetype")
            }

        val preparedUri = preparedFilePair.first
        val preparedFileSize = preparedFilePair.second

        val source = if (fileExtension == "zip") {
            DSUInstallationSource.DsuPackage(preparedUri)
        } else {
            DSUInstallationSource.SingleSystemImage(preparedUri, preparedFileSize)
        }

        onStepUpdate(InstallationStep.WAITING_USER_CONFIRMATION)

        storageManager.cleanWorkspaceFolder(job.isCancelled)

        if (!job.isCancelled) {
            onPreparationFinished(source)
        } else {
            onCanceled()
        }
    }

    private fun prepareZip(zipFile: Uri): Pair<Uri, Long> {
        val uri = getSafeUri(zipFile)
        return Pair(uri, -1)
    }

    private fun prepareXz(xzFile: Uri): Pair<Uri, Long> {
        val outputFile = getFileName(xzFile)
        onStepUpdate(InstallationStep.DECOMPRESSING_XZ)
        val imgFile = FileUnPacker(
            storageManager,
            xzFile,
            outputFile,
            job,
            onPreparationProgressUpdate,
        ).unpack()
        return prepareImage(imgFile.first)
    }

    private fun prepareImage(imageFile: Uri): Pair<Uri, Long> {
        val outputFile = "${getFileName(imageFile)}.img.gz"
        onStepUpdate(InstallationStep.COMPRESSING_TO_GZ)
        val compressedFilePair = FileUnPacker(
            storageManager,
            imageFile,
            outputFile,
            job,
            onPreparationProgressUpdate,
        ).pack()
        return Pair(compressedFilePair.first, storageManager.getFilesizeFromUri(imageFile))
    }

    private fun prepareGz(gzFile: Uri): Pair<Uri, Long> {
        val uri = getSafeUri(gzFile)
        if (userSelectedImageSize != DSUConstants.DEFAULT_IMAGE_SIZE) {
            return Pair(uri, userSelectedImageSize)
        }

        onStepUpdate(InstallationStep.PROCESSING)
        val fileSize = storageManager.getFilesizeFromUri(uri)
        val three_gb = Int.MAX_VALUE.toLong() * 1.5 // 2^32 * 0.75

        // If the .gz is smaller than 3gb, then try returning the image size
        // by reading the lasts four bytes.
        if (fileSize < three_gb) {
            val inputStream = storageManager.openInputStream(uri)
            inputStream.skip(fileSize - 4)
            val bytes = ByteArray(4)
            inputStream.read(bytes)
            bytes.reverse() // Little endian -> Big endian
            val imageSize = BigInteger(1, bytes).toLong()
            // If the image size is LOWER than the compressed file, then
            // the image size must be wrong.
            if (imageSize > fileSize) {
                return Pair(uri, imageSize)
            }
        }
        // If the .gz is bigger than 3gb or the fast-way returns a
        // wrong value, we need to decompress the file and calculate
        // the size. SLOWWWWWWWWWWWWWW
        val outputFile = getFileName(uri)
        onStepUpdate(InstallationStep.DECOMPRESSING_GZIP)
        val extractedFilePair =
            FileUnPacker(storageManager, uri, outputFile, job, onPreparationProgressUpdate).unpack()
        return Pair(uri, extractedFilePair.second)
    }

    private fun extractFile(uri: Uri): Pair<Uri, Long> {
        return extractFile(uri, "system")
    }

    private fun extractFile(uri: Uri, partitionName: String): Pair<Uri, Long> {
        onStepUpdate(InstallationStep.EXTRACTING_FILE)
        return FileUnPacker(
            storageManager,
            uri,
            "$partitionName.img",
            job,
            onPreparationProgressUpdate,
        ).unpack()
    }

    private fun getSafeUri(uri: Uri): Uri {
        onStepUpdate(InstallationStep.COPYING_FILE)
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
}
