package vegabobo.dsusideloader.dsuhelper

import android.content.Context
import android.content.Intent
import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.LogsActivity
import vegabobo.dsusideloader.checks.OperationMode
import vegabobo.dsusideloader.util.SPUtils

class Deploy(
    val context: Context,
    val gsi: GSI,
) {

    fun inst() {
        when (OperationMode.getOperationMode()) {
            OperationMode.Constants.ROOT_MAGISK, OperationMode.Constants.OTHER_ROOT_SOLUTION -> {
                val dsuCommand = DSUCommand(gsi, context, true)
                if (SPUtils.isDebugModeEnabled(context)) {
                    context.startActivity(
                        Intent(context, LogsActivity::class.java).putExtra(
                            "script",
                            dsuCommand.getInstallScript()
                        ).putExtra(
                            "installation_info",
                            dsuCommand.installationInfoAsString()
                        )
                    )
                } else {
                    //showFinishedDialog()
                    Shell.cmd(dsuCommand.getInstallScript()).exec()
                }
            }
            OperationMode.Constants.UNROOTED -> {
//                val dsuCommand = DSUCommand(dsu!!, c, false)
//                showAdbCommandToDeployGSI(dsuCommand.writeInstallScript())
            }
        }
    }
}