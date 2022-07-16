package vegabobo.dsusideloader.preparation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CompletableJob
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.InputStream
import java.io.OutputStream

class UnPack(
    private val storageManager: StorageManager,
    private val inputFile: Uri,
    outputFile: String,
    private val installationJob: CompletableJob,
    private val onProgressChange: (Float) -> Unit
) {

    object Constants {
        const val WORKSPACE_FOLDER = "workspace_dsuhelper"
    }

    private var finalFile: DocumentFile = storageManager.createDocumentFile(outputFile)

    private var outputStream = storageManager.openOutputStream(finalFile.uri)
    private var inputStream = storageManager.openInputStream(inputFile)
    private val inputFileSize = storageManager.getFilesizeFromUri(inputFile)

    private fun copy(
        inputStr: InputStream,
        outputStr: OutputStream,
        onReadedBuffer: (Long) -> Unit
    ) {
        val buffer = ByteArray(8 * 1024)
        var n: Int
        var readed: Long = 0
        while (-1 != inputStr.read(buffer)
                .also { n = it } && !installationJob.isCancelled
        ) {
            readed += buffer.size
            onReadedBuffer(readed)
            outputStr.write(buffer, 0, n)
        }
        inputStr.close()
        outputStr.flush()
        outputStr.close()
    }

    fun pack(): Uri {
        copy(inputStream, GzipCompressorOutputStream(outputStream)) {
            updateProgress(inputFileSize, it)
        }
        return finalFile.uri
    }

    fun unpack(): Uri {
        val archiveInputStream =
            with(storageManager.getFilenameFromUri(inputFile)) {
                when {
                    endsWith("xz") -> XZCompressorInputStream(inputStream)
                    endsWith("gz") -> GzipCompressorInputStream(inputStream)
                    else -> return Uri.EMPTY
                }
            }
        copy(archiveInputStream, outputStream) {
            updateProgress(inputFileSize, archiveInputStream.compressedCount)
        }
        return finalFile.uri
    }

    private fun updateProgress(fileSize: Long, readed: Long) {
        val percent: Float = (readed * 1f) / fileSize
        onProgressChange(percent)
    }

}