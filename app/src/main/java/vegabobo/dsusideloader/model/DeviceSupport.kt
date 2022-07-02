package vegabobo.dsusideloader.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class DeviceSupport(
    val hasDynamicPartitions: MutableState<Boolean> = mutableStateOf(false),
    val hasFreeStorage: MutableState<Boolean> = mutableStateOf(false),
    val hasSetupStorageAccess: MutableState<Boolean> = mutableStateOf(false)
) {

    fun hasDynamicPartitions(): Boolean {
        return hasDynamicPartitions.value
    }

    fun hasFreeStorage(): Boolean {
        return hasFreeStorage.value
    }

    fun hasSetupStorageAccess(): Boolean {
        return hasSetupStorageAccess.value
    }

    fun isCompatible(): Boolean {
        return hasDynamicPartitions.value && hasFreeStorage.value && hasSetupStorageAccess.value
    }
}