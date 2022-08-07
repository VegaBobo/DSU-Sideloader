package vegabobo.dsusideloader.privilegedservice

import android.app.IActivityManager
import android.content.Intent
import android.content.pm.IPackageManager
import android.os.IBinder
import android.os.Process
import android.os.storage.IStorageManager
import android.os.storage.VolumeInfo
import org.lsposed.hiddenapibypass.HiddenApiBypass
import vegabobo.dsusideloader.BuildConfig
import vegabobo.dsusideloader.IPrivilegedService
import kotlin.system.exitProcess

class PrivilegedService : IPrivilegedService.Stub() {

    override fun exit() {
        destroy()
    }

    override fun destroy() {
        exitProcess(0);
    }

    private fun getBinder(service: String): IBinder {
        val serviceManager = Class.forName("android.os.ServiceManager")
        val binder = HiddenApiBypass.invoke(serviceManager, null, "getService", service)
        return binder as IBinder
    }

    override fun getUid(): Int {
        return Process.myUid()
    }

    //
    // Activity Manager
    //

    private var ACTIVITY_MANAGER: IActivityManager? = null

    private fun requiresActivityManager() {
        if (ACTIVITY_MANAGER == null)
            ACTIVITY_MANAGER = IActivityManager.Stub.asInterface(getBinder("activity"))
    }

    override fun startActivity(intent: Intent?) {
        requiresActivityManager()
        val callerPackage =
            if (uid == 2000 || uid == 0) "com.android.shell" else BuildConfig.APPLICATION_ID
        ACTIVITY_MANAGER!!.startActivityAsUserWithFeature(
            null,
            callerPackage,
            null,
            intent,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            0
        )
    }

    //
    // Package Manager
    //

    private var PACKAGE_MANAGER: IPackageManager? = null

    private fun requiresPackageManager() {
        if (PACKAGE_MANAGER == null)
            PACKAGE_MANAGER = IPackageManager.Stub.asInterface(getBinder("package"))
    }

    override fun grantPermission(permissionName: String?) {
        requiresPackageManager()
        PACKAGE_MANAGER!!.grantRuntimePermission(BuildConfig.APPLICATION_ID, permissionName, 0)
    }

    //
    // Storage Manager
    //

    private var STORAGE_MANAGER: IStorageManager? = null

    private fun requiresStorageManager() {
        if (STORAGE_MANAGER == null)
            STORAGE_MANAGER = IStorageManager.Stub.asInterface(getBinder("mount"))
    }

    override fun getVolumes(): List<VolumeInfo> {
        requiresStorageManager()
        val vols = ArrayList<VolumeInfo>()
        vols.addAll(STORAGE_MANAGER!!.getVolumes(0))
        return vols
    }

    override fun unmount(volId: String?) {
        requiresStorageManager()
        STORAGE_MANAGER!!.unmount(volId)
    }

    override fun mount(volId: String?) {
        requiresStorageManager()
        STORAGE_MANAGER!!.mount(volId)
    }

}