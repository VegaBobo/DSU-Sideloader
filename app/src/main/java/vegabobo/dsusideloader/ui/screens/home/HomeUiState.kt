package vegabobo.dsusideloader.ui.screens.home

data class HomeUiState(

    // Installation card
    val installationFieldText: String = "",
    val isInstallationFieldEnabled: Boolean = true,
    val isInstallable: Boolean = false,

    // Userdata card
    val isCustomUserdataSelected: Boolean = false,
    val isCustomUserdataError: Boolean = false,
    val userdataFieldText: String = "",
    val maximumAllowedAlloc: Int = 0,

    // ImageSize card
    val isCustomImageSizeSelected: Boolean = false,
    val showImageSizeDialog: Boolean = false,
    val imageSizeFieldText: String = "",

    // Warning cards
    val showSetupStorageCard: Boolean = false,
    val showLowStorageCard: Boolean = false,
    val showUnsupportedCard: Boolean = false,

    // Installation
    val showInstallationDialog: Boolean = false,
    val isInstalling: Boolean = false,
    val installationStep: Int = -1,
    val installationProgress: Float = 0.0f,
    val showCancelDialog: Boolean = false,

    // Keep screen on
    val keepScreenOn: Boolean = false
)