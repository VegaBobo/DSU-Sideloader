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

    }

}