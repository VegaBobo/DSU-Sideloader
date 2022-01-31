package vegabobo.dsusideloader.dsuhelper

import com.topjohnwu.superuser.Shell

class RootDSUDeployer(private val gsiDsuObject: GsiDsuObject) {

    init {
        startDSUActivity()
    }

    private fun startDSUActivity() {
        if (!Shell.su("getprop persist.sys.fflag.override.settings_dynamic_system")
                .exec().out[0].toBoolean()
        ) {
            Shell.su("setprop persist.sys.fflag.override.settings_dynamic_system true").exec()
            startDSUActivity()
        } else {
            Shell.su(
                "am start-activity -n com.android.dynsystem/com.android.dynsystem.VerificationActivity " +
                        "-a android.os.image.action.START_INSTALL " +
                        "-d ${gsiDsuObject.absolutePath} " +
                        "--el KEY_SYSTEM_SIZE ${gsiDsuObject.fileSize} " +
                        "--el KEY_USERDATA_SIZE ${gsiDsuObject.getUserdataInBytes()}"
            ).exec()
        }
    }
}