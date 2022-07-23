package vegabobo.dsusideloader.installation

import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.preparation.StorageManager

class Deploy(
    storageManager: StorageManager,
    gsi: TargetGSI
) {

    private val installationCommand = GenInstallation(gsi, storageManager)

    fun startInstallationRooted() {
        Shell.cmd(installationCommand.getShellScript(true)).exec()
    }

    fun getInstallationCommand(): String {
        return installationCommand.writeInstallScript(false)
    }
}