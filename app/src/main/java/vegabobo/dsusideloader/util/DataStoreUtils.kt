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
            onFinish: (Boolean) -> Unit = {}
        ) {
            val value = dataStore.data.map {
                it[booleanPreferencesKey(key)] ?: default
            }.first()
            onFinish(value)
        }

        suspend fun readStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: String,
            onFinish: (String) -> Unit = {}
        ) {
            val value = dataStore.data.map {
                it[stringPreferencesKey(key)] ?: default
            }.first()
            onFinish(value)
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

        suspend fun updateStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: String,
            onFinish: () -> Unit = {}
        ) {
            dataStore.edit {
                it[stringPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

    }

}