package vegabobo.dsusideloader.installation

import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.preparation.StorageManager

class Deploy(
    private val storageManager: StorageManager,
    val gsi: TargetGSI
) : () -> Unit {

    override fun invoke() {
        val installationCommand = GenInstallation(gsi, storageManager, true)
        when (OperationMode.getOperationMode()) {
            OperationMode.Constants.ROOT_MAGISK, OperationMode.Constants.OTHER_ROOT_SOLUTION -> {
                Shell.cmd(installationCommand.getInstallScript()).exec()
            }
            OperationMode.Constants.UNROOTED -> {
                //installationCommand.writeInstallScript()
            }
        }
    }

    fun startInstallation() {
        when (OperationMode.getOperationMode()) {
            OperationMode.Constants.ROOT_MAGISK, OperationMode.Constants.OTHER_ROOT_SOLUTION -> {
                val dsuCommand = GenInstallation(gsi, storageManager, true)
                Shell.cmd(dsuCommand.getInstallScript()).exec()
//                if (isDebugMode) {
//                    context.startActivity(
//                        Intent(context, LogsActivity::class.java).putExtra(
//                            "script",
//                            dsuCommand.getInstallScript()
//                        ).putExtra(
//                            "installation_info",
//                            dsuCommand.installationInfoAsString()
//                        )
//                    )
//                } else {
//                    //showFinishedDialog()
//                    Shell.cmd(dsuCommand.getInstallScript()).exec()
//                }
            }
            OperationMode.Constants.UNROOTED -> {
//                val dsuCommand = DSUCommand(dsu!!, c, false)
//                showAdbCommandToDeployGSI(dsuCommand.writeInstallScript())
            }
        }
    }
}