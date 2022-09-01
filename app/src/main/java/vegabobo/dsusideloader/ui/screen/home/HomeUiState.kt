package vegabobo.dsusideloader.ui.screen.home

import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.util.FilenameUtils

data class InstallationCardState(
    val isTextFieldEnabled: Boolean = true,
    val isInstallable: Boolean = false,
    val isError: Boolean = false,
    val isIndeterminate: Boolean = false,
    val content: String = "",
    val installationStep: InstallationStep = InstallationStep.NOT_INSTALLING,
    val errorContent: String = "",
    val workingPartition: String = "",
    val installationProgress: Float = 0F,
)

data class UserDataCardState(
    val isSelected: Boolean = false,
    val isError: Boolean = false,
    val content: String = "",
    val maximumAllowed: Int = 0,
) {
    fun getAllowedValue(): String {
        if(content.isEmpty() || content == "0GB")
            return ""
        if(FilenameUtils.getDigits(content).toInt() > maximumAllowed)
            return FilenameUtils.appendToString("$maximumAllowed","GB")
        return content
    }
}

data class ImageSizeCardState(
    val isSelected: Boolean = false,
    val content: String = "",
)

enum class AdditionalCard {
    NONE,
    SETUP_STORAGE,
    UNAVAIABLE_STORAGE,
    NO_DYNAMIC_PARTITIONS,
    MISSING_READ_LOGS_PERMISSION,
    GRANTING_READ_LOGS_PERMISSION,
}

enum class DialogDisplay {
    NONE,
    IMAGESIZE_WARNING,
    CONFIRM_INSTALLATION,
    CANCEL_INSTALLATION,
    DISCARD_DSU
}

enum class HomeViewAction {
    NONE,
    NAVIGATE_TO_ADB_SCREEN
}

data class HomeUiState(
    val installationCard: InstallationCardState = InstallationCardState(),
    val userDataCard: UserDataCardState = UserDataCardState(),
    val imageSizeCard: ImageSizeCardState = ImageSizeCardState(),
    val additionalCard: AdditionalCard = AdditionalCard.NONE,
    val dialogDisplay: DialogDisplay = DialogDisplay.NONE,
    val isLogging: Boolean = false,
    val canInstall: Boolean = false,
    val shouldKeepScreenOn: Boolean = false,
) {
    fun isInstalling(): Boolean {
        return installationCard.installationStep != InstallationStep.NOT_INSTALLING
    }
}