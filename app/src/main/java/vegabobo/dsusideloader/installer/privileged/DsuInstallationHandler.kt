package vegabobo.dsusideloader.installer.privileged

import android.content.Intent
import android.os.storage.VolumeInfo
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.service.PrivilegedProvider

/**
 * Install images via DSU app
 * Supported modes are: Shizuku (as shell or root), root and system
 */
open class DsuInstallationHandler(
    private val session: Session,
) {

    private val tag = this.javaClass.simpleName

    fun startInstallation() {
        if (session.preferences.isUnmountSdCard) {
            unmountSdTemporary()
        }
        forwardInstallationToDSU()
    }

    private fun forwardInstallationToDSU() {
        val userdataSize = session.userSelection.userSelectedUserdata
        val fileUri = session.dsuInstallation.uri
        val length = session.dsuInstallation.fileSize

        PrivilegedProvider.run {
            setDynProp()
            forceStopPackage("com.android.dynsystem")

            val dynIntent = Intent()
            dynIntent.setClassName(
                "com.android.dynsystem",
                "com.android.dynsystem.VerificationActivity",
            )
            dynIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            dynIntent.action = "android.os.image.action.START_INSTALL"
            dynIntent.data = fileUri
            dynIntent.putExtra("KEY_USERDATA_SIZE", userdataSize)
            dynIntent.putExtra("KEY_SYSTEM_SIZE", length)

            Log.d(tag, "Starting DSU VerificationActivity: $dynIntent")
            startActivity(dynIntent)
        }
    }

    private fun unmountSdTemporary() {
        val volumes: List<VolumeInfo> =
            PrivilegedProvider.getService().volumes
        val volumesUnmount: ArrayList<String> = ArrayList()
        for (volume in volumes)
            if (volume.id.contains("public")) {
                PrivilegedProvider.run { unmount(volume.id) }
                volumesUnmount.add(volume.id)
                Log.d(tag, "Volume unmounted: ${volume.id}")
            }
        if (volumesUnmount.size > 0) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                delay(30 * 1000)
                for (volume in volumesUnmount) {
                    Log.d(tag, "Volume remounted: $volume")
                    PrivilegedProvider.run { mount(volume) }
                }
            }
        }
    }
}
