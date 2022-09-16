package vegabobo.dsusideloader.ui.screen.adb

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vegabobo.dsusideloader.model.Session
import javax.inject.Inject

data class AdbUiState(
    val buttonCopyText1: Boolean = false,
    val buttonCopyText2: Boolean = false,
    val isShowingExitSheet: Boolean = false,
)

@HiltViewModel
class AdbViewModel @Inject constructor(
    private val session: Session
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdbUiState())
    val uiState: StateFlow<AdbUiState> = _uiState.asStateFlow()

    fun obtainScriptPath(): String = session.installationScript

    fun onBackPressed() {
        _uiState.update { it.copy(isShowingExitSheet = true) }
    }

    fun onClickCancelDialog() {
        _uiState.update { it.copy(isShowingExitSheet = false) }
    }

}
