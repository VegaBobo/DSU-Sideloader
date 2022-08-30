package vegabobo.dsusideloader.ui.screen.settings

data class SettingsUiState(
    val isShowingBuiltinInstallerDialog: Boolean = false,
    val useBuiltinInstaller: Boolean = false,
    val keepScreenOn: Boolean = false,
    val umountSd: Boolean = true,
    val isRoot: Boolean = false,
)