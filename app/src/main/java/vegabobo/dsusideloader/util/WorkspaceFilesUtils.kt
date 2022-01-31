package vegabobo.dsusideloader.util

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

class WorkspaceFilesUtils {

    companion object {

        fun getWorkspaceFolder(context: Context): DocumentFile {
            val workspaceFolder =
                DocumentFile.fromTreeUri(context, (SPUtils.getSafRwPath(context)).toUri())!!
            if (workspaceFolder.findFile(DeCompressionUtils.Constants.WORKSPACE_FOLDER) == null)
                workspaceFolder.createDirectory(DeCompressionUtils.Constants.WORKSPACE_FOLDER)!!

            return workspaceFolder.findFile(DeCompressionUtils.Constants.WORKSPACE_FOLDER)!!
        }

        fun cleanWorkspaceFolder(context: Context, deleteAlsoGzFile: Boolean) {
            for (i in getWorkspaceFolder(context).listFiles()) {
                if (deleteAlsoGzFile || !i.name.toString().endsWith("gz"))
                    i.delete()
            }
        }

    }

}