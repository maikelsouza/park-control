package com.parkcontrol.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

/** Generic DataStore extension for Preferences */
val Context.settingsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

/** Helper to read Preferences as a Flow with error handling */
fun Context.settingsPreferencesFlow(): Flow<Preferences> =
	settingsPreferencesDataStore.data.catch { exception ->
		if (exception is IOException) {
			emit(emptyPreferences())
		} else {
			throw exception
		}
	}
