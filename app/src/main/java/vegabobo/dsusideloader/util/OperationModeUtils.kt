package vegabobo.dsusideloader.util

import android.content.Context
import android.content.pm.PackageManager
import com.topjohnwu.superuser.Shell
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

enum class OperationMode {
    SYSTEM,
    ROOT,
    SHIZUKU,
    UNROOTED,
}

class OperationModeUtils {

    companion object {

        fun getOperationMode(context: Context, checkShizuku: Boolean): OperationMode {

            // Priorize system mode
            // it should operate with our custom gsid binary
            if (isDsuPermissionGranted(context))
                return OperationMode.SYSTEM

            if (Shell.getShell().isRoot)
                return OperationMode.ROOT

            if (checkShizuku && isShizukuPermissionGranted(context))
                return OperationMode.SHIZUKU

            return OperationMode.UNROOTED
        }

        fun getOperationModeAsString(operationMode: OperationMode): String {
            return when (operationMode) {
                OperationMode.SYSTEM -> "System"
                OperationMode.ROOT -> "Root"
                OperationMode.UNROOTED -> "ADB"
                OperationMode.SHIZUKU -> "Shizuku"
            }
        }

        fun isDsuPermissionGranted(context: Context): Boolean {
            val dynPermission = "android.permission.INSTALL_DYNAMIC_SYSTEM"
            return context.checkCallingOrSelfPermission(dynPermission) == PackageManager.PERMISSION_GRANTED
        }

        fun isShizukuPermissionGranted(context: Context): Boolean {
            return if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                context.checkSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        }

    }

}