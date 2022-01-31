package vegabobo.dsusideloader.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

class FilenameUtils {

    companion object {

        fun getFilePath(uri: Uri, addQuotes: Boolean): String {
            val input = uri.path.toString()
            val safStorage = input.split("/document/")[1].replace("/tree/", "")
            val path = safStorage.split(":")[1]
            return if (safStorage.contains("primary")) {
                return if (addQuotes) {
                    "'file:///storage/emulated/0/$path'"
                } else {
                    "file:///storage/emulated/0/$path"
                }
            } else {
                if (addQuotes) {
                    "'file:///storage/emulated/" + safStorage.split(":")[1] + "/$path'"
                } else {
                    "file:///storage/emulated/" + safStorage.split(":")[1] + "/$path"
                }

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

    }

}