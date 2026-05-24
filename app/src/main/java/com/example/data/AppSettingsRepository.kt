package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepository(private val dao: AppSettingsDao) {
    val settingsFlow: Flow<AppSettings> = dao.getSettingsFlow()
        .map { it ?: AppSettings() }

    suspend fun getSettings(): AppSettings {
        return dao.getSettings() ?: AppSettings()
    }

    suspend fun updateCount(count: Int) {
        val current = dao.getSettings() ?: AppSettings()
        dao.saveSettings(current.copy(count = count))
    }

    suspend fun updateSelectedVoice(voiceName: String) {
        val current = dao.getSettings() ?: AppSettings()
        dao.saveSettings(current.copy(selectedVoiceName = voiceName))
    }
}
