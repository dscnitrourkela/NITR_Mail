package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.others.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DataStoreRepository(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveCredential(key: String, value: String) =
        dataStore.edit { settings -> settings[stringPreferencesKey(key)] = value }

    suspend fun saveLastSync(key: String, value: Long) =
        dataStore.edit { settings -> settings[longPreferencesKey(key)] = value }

    suspend fun saveState(key: String, value: Boolean) =
        dataStore.edit { settings -> settings[booleanPreferencesKey(key)] = value }

    fun readCredential(key: String) =
        flow { emit(dataStore.data.first()[stringPreferencesKey(key)]) }

    fun readLastSync(key: String) =
        flow { emit(dataStore.data.first()[longPreferencesKey(key)]) }

    fun readState(key: String) =
        flow { emit(dataStore.data.first()[booleanPreferencesKey(key)]) }

    suspend fun resetLogin() {
        saveCredential(Constants.KEY_CREDENTIAL, Constants.NO_CREDENTIAL)
        saveCredential(Constants.KEY_TOKEN, Constants.NO_TOKEN)
        saveState(Constants.KEY_SHOULD_SYNC, false)
        saveLastSync(Constants.KEY_SYNC_SERVICE, Constants.NO_LAST_SYNC)
        saveLastSync(
            Constants.KEY_LAST_SYNC + context.getString(R.string.inbox),
            Constants.NO_LAST_SYNC
        )
        saveLastSync(
            Constants.KEY_LAST_SYNC + context.getString(R.string.sent),
            Constants.NO_LAST_SYNC
        )
        saveLastSync(
            Constants.KEY_LAST_SYNC + context.getString(R.string.draft),
            Constants.NO_LAST_SYNC
        )
        saveLastSync(
            Constants.KEY_LAST_SYNC + context.getString(R.string.junk),
            Constants.NO_LAST_SYNC
        )
        saveLastSync(
            Constants.KEY_LAST_SYNC + context.getString(R.string.trash),
            Constants.NO_LAST_SYNC
        )
    }
}