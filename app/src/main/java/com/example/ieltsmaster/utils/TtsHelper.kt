package com.example.ieltsmaster.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TtsHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var onUtteranceDone: (() -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TtsHelper", "Failed to instantiate TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.UK)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onUtteranceDone?.invoke()
                    onUtteranceDone = null
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onUtteranceDone?.invoke()
                    onUtteranceDone = null
                }
            })
            isInitialized = true
        } else {
            isInitialized = false
        }
    }

    fun configureForPartner(partner: ConversationalPartner) {
        if (!isInitialized || tts == null) return
        try {
            tts?.setPitch(partner.speechPitch)
            tts?.setSpeechRate(partner.speechRate)
            if (partner == ConversationalPartner.ALEX) {
                tts?.setLanguage(Locale.US)
            } else {
                tts?.setLanguage(Locale.UK)
            }
        } catch (e: Exception) {
            Log.e("TtsHelper", "Failed to configure partner speech properties", e)
        }
    }

    fun setSpeechSpeed(speedMultiplier: Float) {
        if (!isInitialized || tts == null) return
        try {
            tts?.setSpeechRate(speedMultiplier.coerceIn(0.5f, 2.0f))
        } catch (e: Exception) {
            // Ignored
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            Log.w("TtsHelper", "TextToSpeech not ready or unavailable")
            onDone?.invoke()
            return
        }
        try {
            onUtteranceDone = onDone
            val utteranceId = "IELTS_TTS_" + System.currentTimeMillis()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("TtsHelper", "TTS speak failed", e)
            onDone?.invoke()
        }
    }

    fun stop() {
        try {
            onUtteranceDone = null
            tts?.stop()
        } catch (e: Exception) {
            // Ignored
        }
    }

    fun shutdown() {
        try {
            onUtteranceDone = null
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignored
        }
        tts = null
        isInitialized = false
    }
}

