package vegabobo.dsusideloader.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreUtils {

    companion object {

        suspend fun readBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: Boolean,
            onFinish: (Boolean) -> Unit = {}
        ): Boolean {
            val value = dataStore.data.map {
                it[booleanPreferencesKey(key)] ?: default
            }.first()
            onFinish(value)
            return value
        }

        suspend fun updateBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: Boolean,
            onFinish: () -> Unit = {}
        ) {
            dataStore.edit {
                it[booleanPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

    }

}