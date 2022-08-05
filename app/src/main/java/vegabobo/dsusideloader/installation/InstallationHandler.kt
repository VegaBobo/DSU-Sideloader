package vegabobo.dsusideloader.installation

import android.app.Application
import android.content.Intent
import android.os.storage.StorageManager
import android.os.storage.VolumeInfo
import android.util.Log
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.*
import org.lsposed.hiddenapibypass.HiddenApiBypass
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.model.GSI
import vegabobo.dsusideloader.shizuku.SystemServiceApi
import vegabobo.dsusideloader.util.OperationMode

class InstallationHandler(
    private val installationSession: InstallationSession
) {

    fun start(app: Application) {
        if (installationSession.preferences.isUnmountSdCard)
            unmountSdCardTemporary(app)

        if (installationSession.operationMode == OperationMode.ROOT) {
            startActionInstallRoot()
            return
        }

        startActionInstall(app)
    }

    // When running as root
    private fun startActionInstallRoot() {
        Shell.cmd(
            InstallationCmdline(installationSession.gsi).getCmd()
        ).exec()
    }

    // When running as system/shizuku mode
    fun startActionInstall(app: Application) {
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

        if (installationSession.operationMode == OperationMode.SHIZUKU) {
            SystemServiceApi.startActivity(dynIntent)
            return
        }

        app.startActivity(dynIntent)
    }

    private fun unmountSdCardTemporary(app: Application) {
        when (installationSession.operationMode) {
            OperationMode.ROOT -> unmountRoot()
            OperationMode.SHIZUKU -> unmountShizuku()
            OperationMode.SYSTEM -> unmountSystem(app)
        }
    }

    private fun unmountSystem(app: Application) {
        val sm = app.getSystemService("storage") as StorageManager
        val vols = HiddenApiBypass.invoke(sm.javaClass, sm, "getVolumes") as List<VolumeInfo>
        val publicVolumes = ArrayList<String>()
        for (vol in vols)
            if (vol.id.contains("public")) {
                publicVolumes.add(vol.id)
                HiddenApiBypass.invoke(sm.javaClass, sm, "unmount", vol.id)
            }
        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        appScope.launch {
            // remount unmounted volumes after 30s
            delay(30 * 1000)
            for (ejectedVol in publicVolumes)
                HiddenApiBypass.invoke(sm.javaClass, sm, "mount", ejectedVol)
        }
    }

    private fun unmountShizuku() {
        val vols = SystemServiceApi.getVolumes()
        val volsToBeEjected = ArrayList<String>()
        for (volume in vols)
            if (volume.id.contains("public")) {
                volsToBeEjected.add(volume.id)
                SystemServiceApi.ejectVolume(volume.id)
            }
        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        appScope.launch {
            // remount unmounted volumes after 30s
            delay(30 * 1000)
            for (ejectedVol in volsToBeEjected)
                SystemServiceApi.mountVol(ejectedVol)
        }
    }

    private fun unmountRoot() {
        Shell.cmd(*unmountSdCardCmdLine.toTypedArray()).submit()
    }

    private val unmountSdCardCmdLine = listOf(
        "export SDCARD=\$(sm list-volumes | grep -v null | grep public)",
        "sm unmount \$SDCARD",
        "(sleep 30 && sm mount \$SDCARD) &",
    )

}