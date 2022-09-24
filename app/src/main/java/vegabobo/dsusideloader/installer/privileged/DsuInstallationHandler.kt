package vegabobo.dsusideloader.installer.privileged

import android.content.Intent
import android.os.storage.VolumeInfo
import kotlinx.coroutines.*
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.service.PrivilegedProvider

/**
 * Install images via DSU app
 * Supported modes are: Shizuku (as shell or root), root and system
 */
open class DsuInstallationHandler(
    private val session: Session
) {

    fun startInstallation() {
        if (session.preferences.isUnmountSdCard)
            unmountSdTemporary()
        forwardInstallationToDSU()
    }

    private fun forwardInstallationToDSU() {
        val userdataSize = session.userSelection.userSelectedUserdata
        val fileUri = session.dsuInstallation.uri
        val length = session.dsuInstallation.fileLength

        PrivilegedProvider.getService().forceStopPackage("com.android.dynsystem")

        val dynIntent = Intent()
        dynIntent.setClassName(
            "com.android.dynsystem",
            "com.android.dynsystem.VerificationActivity"
        )
        dynIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        dynIntent.action = "android.os.image.action.START_INSTALL"
        dynIntent.data = fileUri
        dynIntent.putExtra("KEY_USERDATA_SIZE", userdataSize)
        dynIntent.putExtra("KEY_SYSTEM_SIZE", length)

        PrivilegedProvider.getService().startActivity(dynIntent)
    }

    private fun unmountSdTemporary() {
        val volumes: List<VolumeInfo> =
            PrivilegedProvider.getService().volumes as List<VolumeInfo>
        val volumesUnmount: ArrayList<String> = ArrayList()
        for (volume in volumes)
            if (volume.id.contains("public")) {
                PrivilegedProvider.getService().unmount(volume.id)
                volumesUnmount.add(volume.id)
            }
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            delay(30 * 1000)
            for (volume in volumesUnmount)
                PrivilegedProvider.getService().mount(volume)
        }
    }

}