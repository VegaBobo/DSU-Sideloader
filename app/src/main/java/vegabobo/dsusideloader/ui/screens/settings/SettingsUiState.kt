package vegabobo.dsusideloader.ui.screens.settings

data class SettingsUiState(
    val operationMode: String = "",
    val debugInstallation: Boolean = false,
    val keepScreenOn: Boolean = false,
    val umountSd: Boolean = true,
)