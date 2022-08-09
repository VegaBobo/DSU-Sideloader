package vegabobo.dsusideloader.ui.screen.settings

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.preferences.UserPreferences
import vegabobo.dsusideloader.privilegedservice.PrivilegedServiceProvider
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.OperationModeUtils
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    override val dataStore: DataStore<Preferences>,
    private val installationSession: InstallationSession,
    val application: Application
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        readBoolPref(UserPreferences.DEBUG_INSTALLATION) { result ->
            _uiState.update { it.copy(debugInstallation = result) }
        }
        readBoolPref(UserPreferences.KEEP_SCREEN_ON) { result ->
            _uiState.update { it.copy(keepScreenOn = result) }
        }
        readBoolPref(UserPreferences.UMOUNT_SD) { result ->
            _uiState.update { it.copy(umountSd = result) }
        }
    }

    fun toggleInstDebug(value: Boolean) {
        if (getOperationMode() == OperationMode.SHIZUKU
            && !OperationModeUtils.isReadLogsPermissionGranted(application)
        )
            _uiState.update { it.copy(isShowingGrantPermWithShizuku = true) }

        updateBoolPref(UserPreferences.DEBUG_INSTALLATION, value) {
            _uiState.update { it.copy(debugInstallation = value) }
        }
    }

    fun toggleKeepScreenOn(value: Boolean) {
        updateBoolPref(UserPreferences.KEEP_SCREEN_ON, value) {
            _uiState.update { it.copy(keepScreenOn = value) }
        }
    }

    fun toggleUmountSd(value: Boolean) {
        updateBoolPref(UserPreferences.UMOUNT_SD, value) {
            _uiState.update { it.copy(umountSd = value) }
        }
    }

    fun getOperationMode(): OperationMode {
        return installationSession.operationMode
    }

    fun onCancelShizukuGrantReadLogs() {
        toggleInstDebug(false)
        _uiState.update { it.copy(isShowingGrantPermWithShizuku = false) }
    }

    fun shizukuGrantReadLogsPermission() {
        PrivilegedServiceProvider.getService().grantPermission("android.permission.READ_LOGS")
    }

}