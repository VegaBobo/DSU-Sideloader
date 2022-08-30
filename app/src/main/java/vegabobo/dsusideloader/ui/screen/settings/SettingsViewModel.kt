package vegabobo.dsusideloader.ui.screen.settings

import android.app.Application
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
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.service.PrivilegedProvider
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
        uiState.value.preferences.forEach { entry ->
            viewModelScope.launch {
                val isEnabled = readBoolPref(entry.key)
                togglePreference(entry.key, isEnabled)
            }
        }
    }

    fun togglePreference(preference: String, value: Boolean) {
        viewModelScope.launch {
            updateBoolPref(preference, value) {
                _uiState.update {
                    val cloneMap = hashMapOf<String, Boolean>()
                    cloneMap.putAll(uiState.value.preferences)
                    cloneMap[preference] = value
                    it.copy(preferences = cloneMap)
                }
            }
        }
    }

    fun updateInstallerDialogState(isShowing: Boolean) {
        _uiState.update { it.copy(isShowingBuiltinInstallerDialog = isShowing) }
    }

    fun checkIfRootIsAvail() {
        viewModelScope.launch(Dispatchers.IO) {
            if (PrivilegedProvider.isRoot())
                _uiState.update { it.copy(isRoot = true) }
        }
    }

    fun checkOperationMode(): String {
        return OperationModeUtils.getOperationModeAsString(installationSession.operationMode)
    }

}