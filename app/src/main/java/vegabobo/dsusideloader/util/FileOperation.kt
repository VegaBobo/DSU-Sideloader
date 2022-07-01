package vegabobo.dsusideloader.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import org.tukaani.xz.XZInputStream
import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class FileOperation(
    private val context: Context,
    private val inputFile: Uri,
    private val outputFile: String,
    private val workspaceFolder: DocumentFile = WorkspaceUtils.getWorkspaceFolder(context)
) {

    object Constants {
        const val WORKSPACE_FOLDER = "workspace_dsuhelper"
    }

    fun extractXZFile(): Uri {
        return try {
            val extractedFile: DocumentFile? =
                workspaceFolder.createFile("application/octet-stream", outputFile)
            val fin = context.contentResolver.openInputStream(inputFile)
            val inp = BufferedInputStream(fin)
            val out = context.contentResolver.openOutputStream(extractedFile!!.uri)
            val xzIn = XZInputStream(inp)
            val buffer = ByteArray(8192)
            var n: Int
            while (-1 != xzIn.read(buffer).also { n = it }) {
                out!!.write(buffer, 0, n)
            }
            out!!.close()
            xzIn.close()
            extractedFile.uri
        } catch (e: Exception) {
            Log.e("extractXZFile", e.stackTraceToString())
            return Uri.EMPTY
        }
    }

    fun compressGzip(): Uri {
        val compressedFile: DocumentFile? =
            workspaceFolder.createFile("application/octet-stream", outputFile)
        return try {
            GZIPOutputStream(
                context.contentResolver.openOutputStream(compressedFile!!.uri)
            ).use { gos ->
                context.contentResolver.openInputStream(inputFile).use { fis ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (fis!!.read(buffer).also { len = it } > 0) {
                        gos.write(buffer, 0, len)
                    }
                }
            }
            compressedFile.uri
        } catch (e: Exception) {
            Log.e("compressGzip", e.stackTraceToString())
            return Uri.EMPTY
        }
    }

    fun decompressGzip(): Uri {
        return try {
            val documentFile: DocumentFile? =
                workspaceFolder.createFile("application/octet-stream", outputFile)
            GZIPInputStream(
                context.contentResolver.openInputStream(inputFile)
            ).use { gis ->
                context.contentResolver.openOutputStream(documentFile!!.uri).use { fos ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (gis.read(buffer).also { len = it } > 0) {
                        fos!!.write(buffer, 0, len)
                    }
                }
            }
            documentFile!!.uri
        } catch (e: Exception) {
            Log.e("decompressGzip", e.stackTraceToString())
            return Uri.EMPTY
        }

    }

}