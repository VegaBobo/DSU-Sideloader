package vegabobo.dsusideloader.installer.root

import android.gsi.GsiProgress
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.image.IDynamicSystemService
import vegabobo.dsusideloader.service.PrivilegedProvider

open class DynamicSystemImpl : IDynamicSystemService {

    override fun asBinder(): IBinder? {
        return null
    }

    override fun getInstallationProgress(): GsiProgress {
        return PrivilegedProvider.getService().installationProgress
    }

    override fun abort(): Boolean {
        return PrivilegedProvider.getService().abort()
    }

    override fun isInUse(): Boolean {
        return PrivilegedProvider.getService().isInUse
    }

    override fun isInstalled(): Boolean {
        return PrivilegedProvider.getService().isInstalled
    }

    override fun isEnabled(): Boolean {
        return PrivilegedProvider.getService().isEnabled
    }

    override fun remove(): Boolean {
        return PrivilegedProvider.getService().remove()
    }

    override fun setEnable(enable: Boolean, oneShot: Boolean): Boolean {
        return PrivilegedProvider.getService().setEnable(enable, oneShot)
    }

    override fun finishInstallation(): Boolean {
        return PrivilegedProvider.getService().finishInstallation()
    }

    override fun startInstallation(dsuSlot: String): Boolean {
        return PrivilegedProvider.getService().startInstallation(dsuSlot)
    }

    override fun createPartition(name: String, size: Long, readOnly: Boolean): Int {
        return PrivilegedProvider.getService().createPartition(name, size, readOnly)
    }

    override fun closePartition(): Boolean {
        return PrivilegedProvider.getService().closePartition()
    }

    override fun setAshmem(fd: ParcelFileDescriptor, size: Long): Boolean {
        return PrivilegedProvider.getService().setAshmem(fd, size)
    }

    override fun submitFromAshmem(bytes: Long): Boolean {
        return PrivilegedProvider.getService().submitFromAshmem(bytes)
    }

    override fun suggestScratchSize(): Long {
        return PrivilegedProvider.getService().suggestScratchSize()
    }

    fun forceStopDSU() {
        PrivilegedProvider.getService().forceStopPackage("com.android.dynsystem")
    }
}
