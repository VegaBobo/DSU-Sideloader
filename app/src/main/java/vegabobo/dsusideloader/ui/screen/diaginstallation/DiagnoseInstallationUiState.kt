package vegabobo.dsusideloader.ui.screen.diaginstallation

enum class FabAction {
    INSTALL, SAVE_LOGS, LOGS_SAVED
}

enum class DiagDialog {
    NONE,
    EXIT,
    ERROR_EXTERNAL_SDCARD_ALLOC,
    ERROR_NO_AVAIL_STORAGE,
    ERROR_F2FS_WRONG_PATH,
    ERROR_EXTENTS,
    ERROR_SELINUX_A10
}

data class DiagUiState(
    val installationLogs: String = "",
    val fabAction: FabAction = FabAction.INSTALL,
    val isShowingDialog: DiagDialog = DiagDialog.NONE,
    val navigateUp: Boolean = false,
)