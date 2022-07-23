package vegabobo.dsusideloader.ui.screens.adb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdbUiState(
    val buttonCopyText1: Boolean = false,
    val buttonCopyText2: Boolean = false,
    val isShowingExitDialog: Boolean = false,
)

enum class TargetButton {
    BTN_COPY_1, BTN_COPY_2
}

class AdbViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdbUiState())
    val uiState: StateFlow<AdbUiState> = _uiState.asStateFlow()

    val navigateBack = MutableStateFlow(false)

    fun onClickCopyCommand(targetButton: TargetButton) {
        when (targetButton) {
            TargetButton.BTN_COPY_1 -> {
                _uiState.update { it.copy(buttonCopyText1 = true) }
                viewModelScope.launch {
                    delay(5000)
                    _uiState.update { it.copy(buttonCopyText1 = false) }
                }
            }
            TargetButton.BTN_COPY_2 -> {
                _uiState.update { it.copy(buttonCopyText2 = true) }
                viewModelScope.launch {
                    delay(5000)
                    _uiState.update { it.copy(buttonCopyText2 = false) }
                }
            }
        }
    }

    fun onBackPressed() {
        _uiState.update { it.copy(isShowingExitDialog = true) }
    }

    fun onClickConfirmClose() {
        navigateBack.update { true }
    }

    fun onClickCancelDialog() {
        _uiState.update { it.copy(isShowingExitDialog = false) }
    }

}
