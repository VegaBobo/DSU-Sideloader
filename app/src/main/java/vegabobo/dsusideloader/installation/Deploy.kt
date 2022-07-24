package vegabobo.dsusideloader.installation

import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.preparation.StorageManager

class Deploy(
    storageManager: StorageManager,
    gsi: TargetGSI
) {

    private val installationCommand = GenInstallation(gsi, storageManager)

    fun getInstallationCmd(): String{
        return installationCommand.getShellScript(true)
    }

    fun startInstallationRoot() {
        Shell.cmd(installationCommand.getShellScript(true)).exec()
    }

    fun getInstallationFIle(): String {
        return installationCommand.writeInstallScript(false)
    }
}