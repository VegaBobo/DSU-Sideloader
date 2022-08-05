package vegabobo.dsusideloader.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.documentfile.provider.DocumentFile

class FilenameUtils {

    companion object {

        fun appendToString(input: String, textToAppend: String): String {
            var newText = input.filter { it.isDigit() } + textToAppend
            if (newText == textToAppend)
                newText = ""
            return newText
        }

        fun getDigits(input: String): String {
            return appendToString(input, "")
        }

        // tries to convert SAF URI into realpath.
        fun getFilePath(uri: Uri, addQuotes: Boolean): String {
            val input = uri.path.toString()
            val safStorage = input.split("/document/")[1].replace("/tree/", "")
            val path = safStorage.split(":")[1]
            if (path.contains("/storage/emulated"))
                return if (addQuotes) "'file://'$path" else "file://$path"
            return if (safStorage.contains("primary")) {
                val storagePath = "file:///storage/emulated/0/"
                val finalPath = "$storagePath$path"
                return if (addQuotes) "'$finalPath'" else finalPath
            } else {
                val storagePath = "file:///storage/"
                val finalPath = storagePath + safStorage.replace(":", "/")
                if (addQuotes) "'$finalPath'" else finalPath
            }
        }

        fun queryName(resolver: ContentResolver, uri: Uri): String {
            val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
            val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name: String = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

        fun getLengthFromFile(context: Context, uri: Uri): Long {
            return DocumentFile.fromSingleUri(context, uri)!!.length()
        }

        fun getFileExtension(input: String): String {
            if(!input.contains(".")) return ""
            return input.substring(input.lastIndexOf("."))
        }

        fun isFileSupported(filename: String): Boolean{
            return when (getFileExtension(filename)){
                ".gz", ".xz", ".zip", ".img" -> true
                else -> false
            }

        }

    }

}