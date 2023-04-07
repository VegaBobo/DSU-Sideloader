package vegabobo.dsusideloader.preparation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Job
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import vegabobo.dsusideloader.core.StorageManager

class FileUnPacker(
    private val storageManager: StorageManager,
    private val inputFile: Uri,
    outputFile: String,
    private val installationJob: Job,
    private val onProgressChange: (Float) -> Unit,
) {

    private var finalFile: DocumentFile = storageManager.createDocumentFile(outputFile)

    private var outputStream = storageManager.openOutputStream(finalFile.uri)
    private var inputStream = storageManager.openInputStream(inputFile)
    private val inputFileSize = storageManager.getFilesizeFromUri(inputFile)

    private fun copy(
        inputStr: InputStream,
        outputStr: OutputStream,
        onReadedBuffer: (Long) -> Unit,
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

    fun pack(): Pair<Uri, Long> {
        copy(inputStream, GzipCompressorOutputStream(outputStream)) {
            updateProgress(inputFileSize, it)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    fun unpack(): Pair<Uri, Long> {
        val archiveInputStream =
            with(storageManager.getFilenameFromUri(inputFile)) {
                when {
                    endsWith("xz") -> XZCompressorInputStream(inputStream)
                    endsWith("gz") -> GzipCompressorInputStream(inputStream)
                    endsWith("gzip") -> GzipCompressorInputStream(inputStream)
                    else -> throw Exception("File type not supported")
                }
            }
        copy(archiveInputStream, outputStream) {
            updateProgress(inputFileSize, archiveInputStream.compressedCount)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    private fun updateProgress(fileSize: Long, readed: Long) {
        val percent: Float = readed.toFloat() / fileSize.toFloat()
        onProgressChange(percent)
    }
}
