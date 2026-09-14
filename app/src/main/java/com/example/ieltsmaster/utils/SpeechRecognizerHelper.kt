package com.example.ieltsmaster.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechRecognizerHelper(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit = {},
        onRmsChanged: (Float) -> Unit = {}
    ) {
        stopListening()

        if (!isAvailable()) {
            onError("Speech recognition is not available on this device. You can type your message.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        onRmsChanged(rmsdB)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network issue (offline mode active)"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Recognition timed out"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                            else -> "Recognition error ($error)"
                        }
                        Log.w("SpeechRecognizerHelper", "Recognition error: $errorMsg ($error)")
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            // Can be used for live preview
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.UK.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.e("SpeechRecognizerHelper", "Failed to start speech recognition", e)
            isListening = false
            onError("Unable to initialize speech recognizer: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                // Ignored
            }
        }
        isListening = false
    }

    fun destroy() {
        stopListening()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignored
        }
        speechRecognizer = null
    }
}
