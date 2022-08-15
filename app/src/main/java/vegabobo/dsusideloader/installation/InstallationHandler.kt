package vegabobo.dsusideloader.installation

import android.content.Intent
import android.os.storage.VolumeInfo
import kotlinx.coroutines.*
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.privapi.PrivilegedProvider
import vegabobo.dsusideloader.util.OperationMode

class InstallationHandler(
    private val session: Session,
    private val storageManager: StorageManager,
    private val onRootlessAdbScriptGenerated: (String) -> Unit = {},
) : () -> Unit {

    override fun invoke() {
        if (session.operationMode == OperationMode.UNROOTED) {
            generateInstallationScript()
            return
        }

        if (session.preferences.isUnmountSdCard)
            unmountSdTemporary()

        forwardInstallationToDSU()
    }

    /**
     * Generate shell script with installation
     * Used only for installing over adb commands
     */
    private fun generateInstallationScript() {
        val installationScriptPath = GenerateInstallationScript(
            storageManager,
            session.getInstallationParameters(),
            session.preferences,
        ).writeToFile()
        onRootlessAdbScriptGenerated(installationScriptPath)
    }

    /**
     * Install images via DSU app
     * Supported modes are: Shizuku, root and system
     */
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
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            delay(30 * 1000)
            for (volume in volumesUnmount)
                PrivilegedProvider.getService().mount(volume)
        }
    }

}