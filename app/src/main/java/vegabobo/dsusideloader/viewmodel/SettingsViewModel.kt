package vegabobo.dsusideloader.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import vegabobo.dsusideloader.checks.OperationMode
import vegabobo.dsusideloader.util.DataStoreUtils
import javax.inject.Inject

object Preference {
    const val DEBUG_INSTALLATION = "debug_installation"
    const val KEEP_SCREEN_ON = "keep_screen_on"
    const val UMOUNT_SD = "umount_sd"
}

data class SettingsUiState(
    val operationMode: String = "",
    val debugInstallation: Boolean = false,
    val keepScreenOn: Boolean = false,
    val umountSd: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // set initial values used by UI
        val debugInstallationToggle = readPreference(Preference.DEBUG_INSTALLATION, false)
        val keepScreenOnToggle = readPreference(Preference.KEEP_SCREEN_ON, false)
        val umountSdToggle = readPreference(Preference.UMOUNT_SD, true)
        val operationMode = OperationMode.getOperationModeAsString()
        _uiState.update {
            it.copy(
                debugInstallation = debugInstallationToggle,
                keepScreenOn = keepScreenOnToggle,
                umountSd = umountSdToggle,
                operationMode = operationMode
            )
        }
    }

    fun toggleInstDebug(value: Boolean) {
        updateSetting(Preference.DEBUG_INSTALLATION, value)
        _uiState.update { it.copy(debugInstallation = value) }
    }

    fun toggleKeepScreenOn(value: Boolean) {
        updateSetting(Preference.KEEP_SCREEN_ON, value)
        _uiState.update { it.copy(keepScreenOn = value) }
    }

    fun toggleUmountSd(value: Boolean) {
        updateSetting(Preference.UMOUNT_SD, value)
        _uiState.update { it.copy(umountSd = value) }
    }

    fun readPreference(debugInstallation: String, defaultValue: Boolean): Boolean {
        var result: Boolean
        runBlocking {
            result = DataStoreUtils.readBoolPref(dataStore, debugInstallation, defaultValue)
        }
        return result
    }

    fun updateSetting(debugInstallation: String, value: Boolean): Boolean {
        runBlocking {
            DataStoreUtils.updateBoolPref(dataStore, debugInstallation, value)
        }
        return value
    }

}