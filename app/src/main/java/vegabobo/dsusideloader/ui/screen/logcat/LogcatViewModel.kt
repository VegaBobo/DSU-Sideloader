package vegabobo.dsusideloader.ui.screen.logcat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.core.StorageManager
import javax.inject.Inject

data class LogcatScreenUIState(
    val installationLogs: String = "",
    val navigateUp: Boolean = false,
)

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val session: Session,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogcatScreenUIState())
    val uiState: StateFlow<LogcatScreenUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (session.logger != null && !uiState.value.navigateUp) {
                _uiState.update { it.copy(installationLogs = session.logger!!.logs) }
                delay(2000)
            }
        }
    }

    fun onClickSaveLogSuccess(uriToSaveLogs: Uri) {
        storageManager.writeStringToUri(session.logger!!.logs, uriToSaveLogs)
    }

}
