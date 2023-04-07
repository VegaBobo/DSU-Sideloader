package vegabobo.dsusideloader.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreUtils {

    companion object {

        suspend fun readBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: Boolean,
        ): Boolean {
            return dataStore.data.map {
                it[booleanPreferencesKey(key)] ?: default
            }.first()
        }

        suspend fun readStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: String,
        ): String {
            return dataStore.data.map {
                it[stringPreferencesKey(key)] ?: default
            }.first().toString()
        }

        suspend fun readStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: String,
            onFinish: (String) -> Unit,
        ) {
            val result = readStringPref(dataStore, key, default)
            onFinish(result)
        }

        suspend fun updateBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: Boolean,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[booleanPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

        suspend fun updateStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: String,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[stringPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }
    }
}
