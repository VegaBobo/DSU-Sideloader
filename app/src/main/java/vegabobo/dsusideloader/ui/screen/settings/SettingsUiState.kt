package vegabobo.dsusideloader.ui.screen.settings

import vegabobo.dsusideloader.preferences.AppPrefs

data class SettingsUiState(
    val preferences: HashMap<String, Boolean> = hashMapOf(
        AppPrefs.USE_BUILTIN_INSTALLER to false,
        AppPrefs.KEEP_SCREEN_ON to false,
        AppPrefs.UMOUNT_SD to false
    ),
    val isShowingBuiltinInstallerDialog: Boolean = false,
    val isRoot: Boolean = false,
)