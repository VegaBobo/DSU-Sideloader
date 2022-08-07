package vegabobo.dsusideloader.installation

import android.content.Intent
import android.os.storage.VolumeInfo
import kotlinx.coroutines.*
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.privilegedservice.PrivilegedServiceProvider

class InstallationHandler(
    private val installationSession: InstallationSession
) {

    fun start() {
        if (installationSession.preferences.isUnmountSdCard)
            unmountSdTemporary()

        startActionInstall()
    }

    // When running as system/shizuku mode
    fun startActionInstall() {
        val dynIntent = Intent()
        dynIntent.setClassName(
            "com.android.dynsystem",
            "com.android.dynsystem.VerificationActivity"
        )
        dynIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        dynIntent.action = "android.os.image.action.START_INSTALL"
        dynIntent.data = installationSession.gsi.uri
        dynIntent.putExtra("KEY_USERDATA_SIZE", installationSession.gsi.userdataSize)
        if (installationSession.gsi.fileSize != GSI.GSIConstants.DEFAULT_FILE_SIZE)
            dynIntent.putExtra("KEY_SYSTEM_SIZE", installationSession.gsi.fileSize)

        PrivilegedServiceProvider.getService().startActivity(dynIntent)
    }

    private fun unmountSdTemporary() {
        val volumes: List<VolumeInfo> =
            PrivilegedServiceProvider.getService().volumes as List<VolumeInfo>
        val volumesUnmount: ArrayList<String> = ArrayList()
        for (volume in volumes)
            if (volume.id.contains("public")) {
                PrivilegedServiceProvider.getService().unmount(volume.id)
                volumesUnmount.add(volume.id)
            }
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            delay(30 * 1000)
            for (volume in volumesUnmount)
                PrivilegedServiceProvider.getService().mount(volume)
        }
    }

}