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
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.preferences.UserPreferences
import vegabobo.dsusideloader.util.OperationMode
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

}