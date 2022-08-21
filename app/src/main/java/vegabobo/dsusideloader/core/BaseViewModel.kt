package vegabobo.dsusideloader.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vegabobo.dsusideloader.util.DataStoreUtils

open class BaseViewModel(
    open val dataStore: DataStore<Preferences>
) : ViewModel() {

    fun readStringPref(
        key: String,
        onRead: (String) -> Unit
    ) {
        viewModelScope.launch {
            DataStoreUtils.readStringPref(dataStore, key, "") { result ->
                onRead(result)
            }
        }
    }

    fun readBoolPrefBlocking(
        key: String
    ): Boolean {
        var preferenceValue = false
        runBlocking {
            DataStoreUtils.readBoolPref(dataStore, key, false) { result ->
                preferenceValue = result
            }
        }
        return preferenceValue
    }

    fun readBoolPref(
        key: String,
        onRead: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            DataStoreUtils.readBoolPref(dataStore, key, false) { result ->
                onRead(result)
            }
        }
    }

    fun updateBoolPref(
        key: String,
        value: Boolean,
        onRead: () -> Unit
    ) {
        viewModelScope.launch {
            DataStoreUtils.updateBoolPref(dataStore, key, value) {
                onRead()
            }
        }
    }

    fun updateStringPref(
        key: String,
        value: String,
        onRead: () -> Unit
    ) {
        viewModelScope.launch {
            DataStoreUtils.updateStringPref(dataStore, key, value) {
                onRead()
            }
        }
    }

}