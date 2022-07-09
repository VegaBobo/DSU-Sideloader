package vegabobo.dsusideloader.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import vegabobo.dsusideloader.viewmodel.HomeViewModel
import java.io.InputStream
import java.io.OutputStream

class FileOperation(
    private val context: Context,
    private val inputFile: Uri,
    outputFile: String,
    private val homeViewModel: HomeViewModel,
    workspaceFolder: DocumentFile = WorkspaceUtils.getWorkspaceFolder(context),
) {

    object Constants {
        const val WORKSPACE_FOLDER = "workspace_dsuhelper"
    }

    private var finalFile: DocumentFile =
        workspaceFolder.createFile("application/octet-stream", outputFile)!!

    private var outputStream = context.contentResolver.openOutputStream(finalFile.uri)!!
    private var inputStream = context.contentResolver.openInputStream(inputFile)!!
    private val inputFileSize = DocumentFile.fromSingleUri(context, inputFile)!!.length()

    private fun copy(
        inputStr: InputStream,
        outputStr: OutputStream,
        onReadedBuffer: (Long) -> Unit
    ) {
        val buffer = ByteArray(8 * 1024)
        var n: Int
        var readed: Long = 0
        while (-1 != inputStr.read(buffer)
                .also { n = it } && !homeViewModel.installationJob.isCancelled
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
            with(FilenameUtils.queryName(context.contentResolver, inputFile)) {
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
        homeViewModel.updateProgress(if (percent > 1f) 1f else percent)
    }

}