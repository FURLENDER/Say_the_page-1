package com.example.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.AppSettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(
    application: Application,
    private val repository: AppSettingsRepository
) : AndroidViewModel(application) {

    private val _russianVoiceNames = MutableStateFlow<List<String>>(emptyList())
    val russianVoiceNames: StateFlow<List<String>> = _russianVoiceNames.asStateFlow()

    private val _ttsReady = MutableStateFlow(false)
    val ttsReady: StateFlow<Boolean> = _ttsReady.asStateFlow()

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _ttsReady.value = true
                val result = tts?.setLanguage(Locale("ru", "RU"))
                viewModelScope.launch {
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        try {
                            android.widget.Toast.makeText(
                                application,
                                "Русский язык не установлен в TTS на этом устройстве. Пожалуйста, скачайте его в настройках синтеза речи.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            // Safe catch for testing or headless environments
                        }
                    }
                }
                updateVoices()
            } else {
                viewModelScope.launch {
                    try {
                        android.widget.Toast.makeText(
                            application,
                            "Ошибка инициализации синтеза речи (TTS) на устройстве.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        // Safe catch
                    }
                }
            }
        }
    }

    private fun updateVoices() {
        val allVoices = tts?.voices
        val ruVoices = allVoices?.filter { it.locale.language == "ru" } ?: emptyList()
        val names = ruVoices.map { it.name }.filter { it.isNotEmpty() }
        _russianVoiceNames.value = names

        viewModelScope.launch {
            val currentSettings = repository.getSettings()
            if (currentSettings.selectedVoiceName.isEmpty() && names.isNotEmpty()) {
                val maleVoice = ruVoices.find { voice ->
                    val name = voice.name.lowercase()
                    name.contains("male") || name.contains("man") || name.contains("dmitry") || name.contains("ru-ru-x-dfz")
                } ?: ruVoices.firstOrNull()

                maleVoice?.let {
                    repository.updateSelectedVoice(it.name)
                }
            }
        }
    }

    fun increment() {
        viewModelScope.launch {
            val newCount = settings.value.count + 1
            repository.updateCount(newCount)
        }
    }

    fun setCustomValue(value: Int) {
        viewModelScope.launch {
            repository.updateCount(value)
        }
    }

    fun selectVoice(voiceName: String) {
        viewModelScope.launch {
            repository.updateSelectedVoice(voiceName)
        }
    }

    fun speak() {
        speakText("страница ${settings.value.count}")
    }

    fun incrementAndSpeak() {
        viewModelScope.launch {
            val newCount = settings.value.count + 1
            repository.updateCount(newCount)
            speakText("страница $newCount")
        }
    }

    fun speakTzitzit() {
        speakText("собираем кисти цицит")
    }

    fun speakAmida() {
        speakText("готовимся к молитве Ами-да")
    }

    private fun speakText(text: String) {
        if (!_ttsReady.value) return
        
        tts?.stop()

        val selectedName = settings.value.selectedVoiceName
        var voiceSet = false
        if (selectedName.isNotEmpty()) {
            val voice = tts?.voices?.find { it.name == selectedName }
            if (voice != null) {
                try {
                    tts?.voice = voice
                    voiceSet = true
                } catch (e: Exception) {
                    // Fallback to default
                }
            }
        }
        
        if (!voiceSet) {
            tts?.setLanguage(Locale("ru", "RU"))
        }

        tts?.setPitch(0.9f)
        tts?.setSpeechRate(1.0f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CounterTTS_${System.currentTimeMillis()}")
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
