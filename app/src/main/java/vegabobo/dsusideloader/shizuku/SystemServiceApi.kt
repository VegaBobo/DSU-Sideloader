package vegabobo.dsusideloader.shizuku

import android.app.IActivityManager
import android.content.Intent
import android.content.pm.IPackageManager
import android.os.storage.IStorageManager
import android.os.storage.VolumeInfo
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class SystemServiceApi {

    companion object {

        private val PACKAGE_MANAGER: IPackageManager = IPackageManager.Stub.asInterface(
            ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService("package")
            )
        )

        fun grantPermission(packageName: String, permission: String, userId: Int) {
            PACKAGE_MANAGER.grantRuntimePermission(packageName, permission, userId)
        }

        private val ACTIVITY_MANAGER: IActivityManager = IActivityManager.Stub.asInterface(
            ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService("activity")
            )
        )

        fun startActivity(intent: Intent) {
            ACTIVITY_MANAGER.startActivityAsUserWithFeature(
                null,
                null,
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

        private val STORAGE_MANAGER: IStorageManager = IStorageManager.Stub.asInterface(
            ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService("mount")
            )
        )

        fun getVolumes(): List<VolumeInfo> {
            val vols = ArrayList<VolumeInfo>()
            vols.addAll(STORAGE_MANAGER.getVolumes(0))
            return vols
        }

        fun ejectVolume(volId: String) {
            STORAGE_MANAGER.unmount(volId)
        }

        fun mountVol(volId: String) {
            STORAGE_MANAGER.mount(volId)
        }

    }

}