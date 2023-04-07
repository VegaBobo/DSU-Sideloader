package vegabobo.dsusideloader.installer.privileged

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.util.CmdRunner

class LogcatDiagnostic(
    private val onInstallationError: (error: InstallationStep, errorInfo: String) -> Unit,
    private val onStepUpdate: (step: InstallationStep) -> Unit,
    private val onInstallationProgressUpdate: (progress: Float, partition: String) -> Unit,
    private val onInstallationSuccess: () -> Unit,
    private val onLogLineReceived: () -> Unit,
) {

    private val tag = this.javaClass.simpleName
    var logs = ""
    val isLogging = AtomicBoolean(false)
    var shouldLogEverything = false

    fun startLogging(prependString: String) {
        if (isLogging.get()) {
            destroy()
        }
        logs = ""
        isLogging.set(true)
        Log.d(tag, "startLogging(), logEveryting: $shouldLogEverything, isLogging: ${isLogging.get()}")
        CmdRunner.run("logcat -c")
        val logCmd =
            if (shouldLogEverything) {
                "logcat"
            } else {
                "logcat -v tag gsid:* *:S DynamicSystemService:* *:S DynamicSystemInstallationService:* *:S DynSystemInstallationService:* *:S"
            }
        CmdRunner.runReadEachLine(logCmd) {
            if (logs.isEmpty()) {
                logs = "$prependString\n"
            }

            if (it.contains("DynamicSystemService") && it.contains("startInstallation")) {
                onStepUpdate(InstallationStep.INSTALLING)
                onInstallationProgressUpdate(0F, "userdata")
            }

            if (!isLogging.get()) {
                return@runReadEachLine
            }

            logs += "$it\n"
            onLogLineReceived()

            /**
             * Cannot install DSU when running a installed DSU.
             */
            if (it.contains("We are already running in DynamicSystem")) {
                onInstallationError(InstallationStep.ERROR_ALREADY_RUNNING_DYN_OS, it)
                destroy()
            }

            /**
             * When realpath fails with permission denied reason
             * probably gsid is trying to allocate into external sdcard
             * however it throws error, likely due selinux denial
             * E gsid    : realpath failed: /mnt/media_rw/AE5C-6D79/dsu: Permission denied
             * Solutions -> install Magisk module (which may include sepolicy rules to fix that) or temporary unmount sdcard
             */
            if (it.contains("realpath failed") &&
                it.contains("Permission denied")
            ) {
                onInstallationError(InstallationStep.ERROR_EXTERNAL_SDCARD_ALLOC, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * gsid requires at least 40% of free storage
             * E gsid    : free space 24% is below the minimum threshold of 40%
             * Solutions -> delete some stuff
             */
            if (it.contains("is below the minimum threshold of")) {
                onInstallationError(InstallationStep.ERROR_NO_AVAIL_STORAGE, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * Some kernels are registering f2fs as f2fs_dev, throwing error
             * for reference: https://cs.android.com/android/platform/superproject/+/android-12.1.0_r8:system/core/fs_mgr/libfiemap/utility.cpp;l=113
             * E gsid    : read failed: /sys/fs/f2fs/dm-4/features: No such file or directory
             * Solutions -> install Magisk module (which include custom gsid binary, that checks f2fs_dev before failing) or fix kernel
             */
            if (it.contains("read failed") &&
                it.contains("No such file or directory") &&
                it.contains("f2fs")
            ) {
                onInstallationError(InstallationStep.ERROR_F2FS_WRONG_PATH, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * Some ROMs may not set required sepolicy rules to get gsid work correctly
             * W Binder:10924_1: type=1400 audit(0.0:51): avc: denied { getattr } for path="/dev/block/mmcblk0p42" \
             * dev="tmpfs" ino=12407 scontext=u:r:gsid:s0 tcontext=u:object_r:mmcblk_device:s0 tclass=blk_file permissive=0
             * E gsid    : Failed to get stat for block device: /dev/block/mmcblk0p42: Permission denied
             * Solutions -> install Magisk module (which may include sepolicy rules to fix that) or setenforce 0
             */
            if (it.contains("Failed to get stat for block device") &&
                it.contains("Permission denied")
            ) {
                onInstallationError(InstallationStep.ERROR_SELINUX, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * Android 10 seems to require a high extents value
             * E gsid : File is too fragmented, needs more than 512 extents.
             * Possible solution -> install Magisk module (not tested at time i've been writing this)
             */
            if (it.contains("File is too fragmented") && it.contains("512")) {
                onInstallationError(InstallationStep.ERROR_EXTENTS, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * DynamicSystemInstallationService: status: NOT_STARTED, cause: INSTALL_CANCELLED
             */
            if (it.contains("NOT_STARTED")) {
                if (it.contains("INSTALL_CANCELLED")) {
                    onInstallationError(InstallationStep.ERROR_CANCELED, it)
                } else {
                    onInstallationError(InstallationStep.ERROR, it)
                }
                destroy()
                return@runReadEachLine
            }

            /**
             * Extract progress from logcat line using regex, sample output:
             * DynamicSystemInstallationService: status: IN_PROGRESS, cause: CAUSE_NOT_SPECIFIED, partition name: system, progress: 1879162880/1891233792
             */
            if (it.contains("IN_PROGRESS")) {
                if (it.contains("progress:") && it.contains("partition name:")) {
                    try {
                        val progressRgx = "(progress: )([\\d+/]+)".toRegex()
                        val partitionRgx = "(partition name: ([a-z+_]+))".toRegex()

                        val progressText = progressRgx.find(it)!!.groupValues[2].split("/")
                        val progress = (progressText[0].toFloat() / progressText[1].toFloat())

                        val partitionText = partitionRgx.find(it)!!.groupValues[2]

                        onInstallationProgressUpdate(progress, partitionText)
                    } catch (_: Exception) {
                        onStepUpdate(InstallationStep.PROCESSING_LOG_READABLE)
                    }
                } else {
                    onStepUpdate(InstallationStep.PROCESSING_LOG_READABLE)
                }
            }

            /**
             * DynamicSystemInstallationService: status: READY, cause: INSTALL_COMPLETED
             */
            if (it.contains("READY") && it.contains("INSTALL_COMPLETED")) {
                onInstallationSuccess()
                destroy()
                return@runReadEachLine
            }

            /**
             * When cancelling, Android 10 only
             * D/DynSystemInstallationService: onStartCommand(): action=com.android.dynsystem.ACTION_CANCEL_INSTALL
             */
            if (it.contains("ACTION_CANCEL_INSTALL")) {
                onInstallationError(InstallationStep.ERROR_CANCELED, it)
                destroy()
                return@runReadEachLine
            }

            /**
             * When installing, Android 10 only
             * D/DynSystemInstallationService: postStatus(): statusCode=2, causeCode=0
             */
            if (it.contains("postStatus(): statusCode=2" /* STATUS_IN_PROGRESS */)) {
                onStepUpdate(InstallationStep.PROCESSING_LOG_READABLE)
            }

            /**
             * When installation succeed, Android 10 only
             * D/DynSystemInstallationService: postStatus(): statusCode=3, causeCode=1
             */
            if (it.contains("postStatus(): statusCode=3" /* STATUS_READY */)) {
                onInstallationSuccess()
                destroy()
                return@runReadEachLine
            }

            if (it.contains("postStatus(): statusCode=1" /* STATUS_NOT_STARTED */)) {
                onInstallationError(InstallationStep.ERROR, it)
                destroy()
                return@runReadEachLine
            }
        }
    }

    fun destroy() {
        CmdRunner.destroy()
        isLogging.set(false)
        Log.d(tag, "destroy(), isLogging: ${isLogging.get()}")
    }
}
