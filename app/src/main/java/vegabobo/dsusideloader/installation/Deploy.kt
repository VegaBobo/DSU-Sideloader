package vegabobo.dsusideloader.installation

import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.preparation.StorageManager

class Deploy(
    private val storageManager: StorageManager,
    val gsi: TargetGSI
) {

    private val installationCommand = GenInstallation(gsi, storageManager, true)

    fun startInstallationRooted(){
        Shell.cmd(installationCommand.getInstallScript()).exec()
    }

    fun getInstallationCommand(): String{
        return installationCommand.writeInstallScript()
    }
}