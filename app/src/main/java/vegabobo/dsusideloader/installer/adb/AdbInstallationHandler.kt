package vegabobo.dsusideloader.installer.adb

import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.model.Session

/**
 * Generate shell script with installation
 * Used only for installing over adb commands
 */
class AdbInstallationHandler(
    private val storageManager: StorageManager,
    val session: Session,
) {
    fun generate(onGenerated: (String) -> Unit) {
        val installationScriptPath = GenerateInstallationScript(
            storageManager,
            session.getInstallationParameters(),
            session.preferences,
        ).writeToFile()
        onGenerated(installationScriptPath)
    }
}
