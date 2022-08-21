package vegabobo.dsusideloader.ui.screen.settings

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.preferences.UserPreferences
import vegabobo.dsusideloader.service.PrivilegedProvider
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.OperationModeUtils
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    override val dataStore: DataStore<Preferences>,
    private val installationSession: Session,
    val application: Application
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        readBoolPref(UserPreferences.KEEP_SCREEN_ON) { result ->
            _uiState.update { it.copy(keepScreenOn = result) }
        }
        readBoolPref(UserPreferences.UMOUNT_SD) { result ->
            _uiState.update { it.copy(umountSd = result) }
        }
        readBoolPref(UserPreferences.USE_BUILTIN_INSTALLER) { result ->
            _uiState.update { it.copy(useBuiltinInstaller = result) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (PrivilegedProvider.isRoot())
                _uiState.update { it.copy(isRoot = true) }
        }
    }

    fun toggleBuiltinInstaller(value: Boolean) {
        updateBoolPref(UserPreferences.USE_BUILTIN_INSTALLER, value) {
            _uiState.update { it.copy(useBuiltinInstaller = value) }
        }
        updateInstallerDialogState(value)
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

    fun updateInstallerDialogState(isShowing: Boolean) {
        _uiState.update { it.copy(isShowingBuiltinInstallerDialog = isShowing) }
    }

    fun checkOperationMode(): String {
        return OperationModeUtils.getOperationModeAsString(installationSession.operationMode)
    }

}