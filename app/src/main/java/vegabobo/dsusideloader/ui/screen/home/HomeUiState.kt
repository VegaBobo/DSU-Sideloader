package vegabobo.dsusideloader.ui.screen.home

import vegabobo.dsusideloader.preparation.PreparationSteps

data class InstallationCardState(
    val isTextFieldEnabled: Boolean = true,
    val isInstallable: Boolean = false,
    val isError: Boolean = false,
    val content: String = "",
    val installationStep: PreparationSteps = PreparationSteps.NONE,
    val installationProgress: Float = 0.0f,
)

data class UserDataCardState(
    val isSelected: Boolean = false,
    val isError: Boolean = false,
    val content: String = "",
    val maximumAllowed: Int = 0,
)

data class ImageSizeCardState(
    val isSelected: Boolean = false,
    val content: String = "",
)

enum class AdditionalCard {
    NONE,
    SETUP_STORAGE,
    UNAVAIABLE_STORAGE,
    NO_DYNAMIC_PARTITIONS,
}

enum class DialogDisplay {
    NONE,
    IMAGESIZE_WARNING,
    CONFIRM_INSTALLATION,
    CANCEL_INSTALLATION
}

enum class HomeViewAction {
    NONE,
    NAVIGATE_TO_ADB_SCREEN,
    NAVIGATE_TO_DIAGNOSE_SCREEN
}

data class HomeUiState(
    val installationCard: InstallationCardState = InstallationCardState(),
    val userDataCard: UserDataCardState = UserDataCardState(),
    val imageSizeCard: ImageSizeCardState = ImageSizeCardState(),
    val additionalCard: AdditionalCard = AdditionalCard.NONE,
    val dialogDisplay: DialogDisplay = DialogDisplay.NONE,
    val isInstalling: Boolean = false,
    val canInstall: Boolean = false,
    val shouldKeepScreenOn: Boolean = false,
)