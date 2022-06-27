package vegabobo.dsusideloader

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object Toggles {
    const val USERDATA_TOGGLE = 0
    const val IMGSIZE_TOGGLE = 1
}

class UserdataCard : ToggleWithText()
class ImageSizeCard : ToggleWithText()
class InstallationDialog : Toggle()

class InstallationCard(
    val isError: MutableState<Boolean> = mutableStateOf(false),
    val isInstallable: MutableState<Boolean> = mutableStateOf(false),
    override val isEnabled: MutableState<Boolean> = mutableStateOf(true)
) : ToggleWithText() {
    fun lock(textFieldContent: String) {
        this.isInstallable.value = true
        this.isEnabled.value = false
        this.text.value = textFieldContent
    }

    fun clear() {
        this.isInstallable.value = false
        this.isEnabled.value = true
        this.text.value = ""
    }
}
