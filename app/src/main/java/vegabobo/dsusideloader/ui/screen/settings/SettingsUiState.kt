package vegabobo.dsusideloader.ui.screen.settings

data class SettingsUiState(
    val isShowingGrantPermWithShizuku: Boolean = false,
    val debugInstallation: Boolean = false,
    val keepScreenOn: Boolean = false,
    val umountSd: Boolean = true,
)