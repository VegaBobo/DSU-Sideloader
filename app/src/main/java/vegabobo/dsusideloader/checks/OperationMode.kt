package vegabobo.dsusideloader.checks

import com.topjohnwu.superuser.Shell

class OperationMode {

    object Constants {
        // running as user app on non-rooted device
        // let user invoke DSU Activity with ADB command
        const val UNROOTED = 0

        // running as privileged system app
        // we can use internal API and invoke DSU as we want
        // not implemented, so, by now, useless.
        // const val SYSTEM = 1

        // on Magisk, only version >= 23018
        const val ROOT_MAGISK = 10

        // on Magisk, unsupported
        const val MAGISK_UNSUPPORTED = -1

        // untested on other root solution, warn not tested
        const val OTHER_ROOT_SOLUTION = 100
    }

    companion object {
        fun getOperationMode(): Int {
            return if (Shell.cmd("whoami").exec().isSuccess) {
                if ("MAGISK" in Shell.cmd("su --version").exec().out.toString()) {
                    if (obtainMagiskVersion() < 23018) {
                        Constants.MAGISK_UNSUPPORTED
                    } else {
                        Constants.ROOT_MAGISK
                    }
                } else {
                    Constants.OTHER_ROOT_SOLUTION
                }
            } else {
                Constants.UNROOTED
            }
        }

        fun obtainMagiskVersion(): Int {
            return Shell.cmd("su -V").exec().out
                .toString()
                .replace("[", "")
                .replace("]", "")
                .toInt()
        }
    }
}
