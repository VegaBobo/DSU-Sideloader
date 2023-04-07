package vegabobo.dsusideloader.ui.screen.settings

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.util.OperationModeUtils

@HiltViewModel
class SettingsViewModel @Inject constructor(
    override val dataStore: DataStore<Preferences>,
    private val session: Session,
    val application: Application,
) : BaseViewModel(dataStore) {

    private val tag = this.javaClass.simpleName

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        uiState.value.preferences.forEach { entry ->
            viewModelScope.launch {
                val isEnabled = readBoolPref(entry.key)
                togglePreference(entry.key, isEnabled)
            }
        }

        if (session.isRoot()) {
            _uiState.update { it.copy(isRoot = true) }
        }
    }

    fun togglePreference(preference: String, value: Boolean) {
        viewModelScope.launch {
            updateBoolPref(preference, value) {
                _uiState.update {
                    val cloneMap = hashMapOf<String, Boolean>()
                    cloneMap.putAll(uiState.value.preferences)
                    cloneMap[preference] = value
                    Log.d(tag, "preference: $preference, isEnabled: $value")
                    it.copy(preferences = cloneMap)
                }
            }
        }
    }

    fun isAndroidQ(): Boolean = Build.VERSION.SDK_INT == 29

    fun updateInstallerSheetState(isShowing: Boolean) {
        _uiState.update { it.copy(isShowingBuiltinInstallerSheet = isShowing) }
    }

    fun checkOperationMode(): String {
        return OperationModeUtils.getOperationModeAsString(session.getOperationMode())
    }
}
