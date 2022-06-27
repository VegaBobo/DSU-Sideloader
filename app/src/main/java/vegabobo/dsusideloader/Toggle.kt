package vegabobo.dsusideloader

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class Toggle(
    open val isEnabled: MutableState<Boolean> = mutableStateOf(false)
) {
    fun toggle() {
        this.isEnabled.value = isEnabled.value.not()
    }

    fun isEnabled(): Boolean {
        return this.isEnabled.value
    }
}

open class ToggleWithText(
    open val text: MutableState<String> = mutableStateOf("")
) : Toggle() {

    fun setText(text: String) {
        this.text.value = text
    }

    fun getText(): String {
        return this.text.value
    }

    fun getDigits(): String {
        return addSuffix(this.text.value, "")
    }

    fun addSuffix(input: String, suffix: String): String {
        var newText = input.filter { it.isDigit() } + suffix
        if (newText == suffix)
            newText = ""
        return newText
    }

    fun isTextEmpty(): Boolean {
        return this.text.value.isEmpty()
    }

    fun isTextNotEmpty(): Boolean {
        return !isTextEmpty()
    }

}