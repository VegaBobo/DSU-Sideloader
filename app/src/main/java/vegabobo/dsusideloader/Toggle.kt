package vegabobo.dsusideloader

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class Toggle(
    open val isEnabled: MutableState<Boolean> = mutableStateOf(false)
) {
    fun toggle() {
        this.isEnabled.value = isEnabled.value.not()
    }
}

open class ToggleWithText(
    open val text: MutableState<String> = mutableStateOf("")
) : Toggle() {

    private fun obtainTextWithSuffix(suffix: String): String {
        return text.value.filter { it.isDigit() } + suffix
    }

    fun obtainText(): String {
        return obtainTextWithSuffix("")
    }

    fun setTextContentSuffix(input: String, suffix: String) {
        text.value = input
        text.value = obtainTextWithSuffix(suffix)
        if (text.value == suffix)
            text.value = ""
    }

    fun isEnabled(): Boolean {
        return this.isEnabled.value
    }

    fun isContentEmpty(): Boolean {
        return this.text.value.isEmpty()
    }

    fun isContentNotEmpty(): Boolean {
        return !isContentEmpty()
    }

}