package vegabobo.dsusideloader.installer.privileged

import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.util.CmdRunner

class LogcatDiagnostic(
    private val onInstallationError: (error: InstallationStep, errorInfo: String) -> Unit,
    private val onStepUpdate: (step: InstallationStep) -> Unit,
    private val onInstallationProgressUpdate: (progress: Float, partition: String) -> Unit,
    private val onInstallationSuccess: () -> Unit,
    private val onLogLineReceived: () -> Unit
) {

    var logs = ""
    var isLogging = false

    fun startLogging() {
        logs = ""
        isLogging = true
        CmdRunner.run("logcat -c")
        CmdRunner.runReadEachLine("logcat --format brief | grep -e gsid -e DynamicSystem | grep -v OUT") {

            if (logs.isEmpty()) {
                onStepUpdate(InstallationStep.INSTALLING)
                onInstallationProgressUpdate(0F, "userdata")
            }

            logs += "$it\n"
            onLogLineReceived()

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
                onInstallationError(InstallationStep.ERROR_EXTERNAL_SDCARD_ALLOC, it)
                return@runReadEachLine
            }

            /**
             * gsid requires at least 40% of free storage
             * E gsid    : free space 24% is below the minimum threshold of 40%
             * Solutions -> delete some stuff
             */
            if (it.contains("is below the minimum threshold of")) {
                destroy()
                onInstallationError(InstallationStep.ERROR_NO_AVAIL_STORAGE, it)
                return@runReadEachLine
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
                onInstallationError(InstallationStep.ERROR_F2FS_WRONG_PATH, it)
                return@runReadEachLine
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
                onInstallationError(InstallationStep.ERROR_SELINUX_A10, it)
                return@runReadEachLine
            }

            /**
             * Android 10 seems to require a high extents value
             * E gsid : File is too fragmented, needs more than 512 extents.
             * Solutions -> run app in system-mode (not tested)
             */
            if (it.contains("File is too fragmented")) {
                destroy()
                onInstallationError(InstallationStep.ERROR_EXTENTS, it)
                return@runReadEachLine
            }

            /**
             * DynamicSystemInstallationService: status: NOT_STARTED, cause: INSTALL_CANCELLED
             */
            if (it.contains("NOT_STARTED")) {
                if (it.contains("INSTALL_CANCELLED"))
                    onInstallationError(InstallationStep.ERROR_CANCELED, it)
                else
                    onInstallationError(InstallationStep.ERROR, it)
                destroy()
                return@runReadEachLine
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

                onInstallationProgressUpdate(progress, partitionText)
            }

            /**
             * DynamicSystemInstallationService: status: READY, cause: INSTALL_COMPLETED
             */
            if (it.contains("READY") && it.contains("INSTALL_COMPLETED")) {
                destroy()
                onInstallationSuccess()
                return@runReadEachLine
            }

        }
    }

    fun destroy() {
        if (isLogging) {
            CmdRunner.destroy()
            isLogging = false
        }
    }

}