package vegabobo.dsusideloader.logging

import vegabobo.dsusideloader.preparation.InstallationSteps
import vegabobo.dsusideloader.util.CmdRunner

interface ILogcatDiagnostic {
    fun onErrorDetected(error: InstallationSteps, errorLine: String)
    fun onProcessUpdate(float: Float, partition: String)
    fun onSuccess()
    fun onCancelled()
}

abstract class LogcatDiagnostic : ILogcatDiagnostic {

    var logs = ""
    var isLogging = false

    fun startLogging() {
        isLogging = true
        CmdRunner.run("logcat -c")
        CmdRunner.runReadEachLine("logcat --format brief | grep -e gsid -e DynamicSystem | grep -v OUT") {

            if (logs.isEmpty())
                onProcessUpdate(0F, "userdata")

            logs += "$it\n"

            /**
             * When realpath fails with permission denied reason
             * probably gsid is trying to allocate into external sdcard
             * however it throws error, likely due selinux denial
             * E gsid    : realpath failed: /mnt/media_rw/AE5C-6D79/dsu: Permission denied
             * Solutions -> run app in system-mode or temporary unmount sdcard
             */
            if (it.contains("realpath failed")
                && it.contains("Permission denied")
            ) {
                destroy()
                onErrorDetected(InstallationSteps.ERROR_EXTERNAL_SDCARD_ALLOC, it)
            }

            /**
             * gsid requires at least 40% of free storage
             * E gsid    : free space 24% is below the minimum threshold of 40%
             * Solutions -> delete some stuff
             */
            if (it.contains("is below the minimum threshold of")) {
                destroy()
                onErrorDetected(InstallationSteps.ERROR_NO_AVAIL_STORAGE, it)
            }

            /**
             * Some kernels are registering f2fs as f2fs_dev, throwing error
             * for reference: https://cs.android.com/android/platform/superproject/+/android-12.1.0_r8:system/core/fs_mgr/libfiemap/utility.cpp;l=113
             * E gsid    : read failed: /sys/fs/f2fs/dm-4/features: No such file or directory
             * Solutions -> run app in system-mode or fix kernel
             */
            if (it.contains("read failed")
                && it.contains("No such file or directory")
                && it.contains("f2fs")
            ) {
                destroy()
                onErrorDetected(InstallationSteps.ERROR_F2FS_WRONG_PATH, it)
            }

            /**
             * Android 10 does not set sepolicy rules for gsid
             * W Binder:10924_1: type=1400 audit(0.0:51): avc: denied { getattr } for path="/dev/block/mmcblk0p42" \
             * dev="tmpfs" ino=12407 scontext=u:r:gsid:s0 tcontext=u:object_r:mmcblk_device:s0 tclass=blk_file permissive=0
             * E gsid    : Failed to get stat for block device: /dev/block/mmcblk0p42: Permission denied
             * Solutions -> run app in system-mode or setenforce 0
             */
            if (it.contains("Failed to get stat for block device")
                && it.contains("Permission denied")
            ) {
                destroy()
                onErrorDetected(InstallationSteps.ERROR_SELINUX_A10, it)
            }

            /**
             * Android 10 seems to require a high extents value
             * E gsid : File is too fragmented, needs more than 512 extents.
             * Solutions -> run app in system-mode (not tested)
             */
            if (it.contains("File is too fragmented")) {
                destroy()
                onErrorDetected(InstallationSteps.ERROR_EXTENTS, it)
            }

            /**
             * DynamicSystemInstallationService: status: NOT_STARTED, cause: INSTALL_CANCELLED
             */
            if (it.contains("NOT_STARTED")) {
                if (it.contains("INSTALL_CANCELLED"))
                    onErrorDetected(InstallationSteps.CANCELED, it)
                onErrorDetected(InstallationSteps.ERROR_GENERIC_ERROR, it)
            }

            /**
             * Extract progress from logcat line using regex, sample output:
             * DynamicSystemInstallationService: status: IN_PROGRESS, cause: CAUSE_NOT_SPECIFIED, partition name: system, progress: 1879162880/1891233792
             */
            if (it.contains("IN_PROGRESS")) {
                val progressRgx = "(progress: )([\\d+/]+)".toRegex()
                val partitionRgx = "(partition name: ([a-z+_]+))".toRegex()

                val progressText = progressRgx.find(it)!!.groupValues[2].split("/")
                val progress = (progressText[0].toFloat() / progressText[1].toFloat())

                val partitionText = partitionRgx.find(it)!!.groupValues[2]

                onProcessUpdate(progress, partitionText)
            }

            /**
             * DynamicSystemInstallationService: status: READY, cause: INSTALL_COMPLETED
             */
            if (it.contains("READY") && it.contains("INSTALL_COMPLETED")) {
                onSuccess()
            }

        }
    }

    fun destroy() {
        logs = ""
        if (isLogging) {
            CmdRunner.destroy()
            isLogging = false
        }
    }

    override fun onErrorDetected(error: InstallationSteps, errorLine: String) {
        destroy()
    }

    override fun onSuccess() {
        destroy()
    }

    override fun onCancelled() {
        destroy()
    }

}