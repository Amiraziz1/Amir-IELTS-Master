package com.example.ieltsmaster.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentRecordingFile: File? = null

    var isRecording: Boolean = false
        private set

    var isPlaying: Boolean = false
        private set

    fun startRecording(taskId: String): Boolean {
        stopPlayback()
        stopRecording()

        try {
            val audioDir = File(context.filesDir, "speaking_recordings")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            val audioFile = File(audioDir, "rec_${taskId}.m4a")
            currentRecordingFile = audioFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            isRecording = true
            return true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to start audio recording", e)
            isRecording = false
            mediaRecorder?.release()
            mediaRecorder = null
            return false
        }
    }

    fun stopRecording(): File? {
        if (!isRecording) return currentRecordingFile
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error stopping recorder", e)
        } finally {
            mediaRecorder = null
            isRecording = false
        }
        return currentRecordingFile
    }

    fun hasRecording(taskId: String): Boolean {
        val audioDir = File(context.filesDir, "speaking_recordings")
        val audioFile = File(audioDir, "rec_${taskId}.m4a")
        return audioFile.exists() && audioFile.length() > 0
    }

    fun startPlayback(taskId: String, onCompletion: () -> Unit = {}): Boolean {
        stopPlayback()
        val audioDir = File(context.filesDir, "speaking_recordings")
        val audioFile = File(audioDir, "rec_${taskId}.m4a")
        if (!audioFile.exists() || audioFile.length() == 0L) {
            return false
        }

        try {
            val player = MediaPlayer()
            player.setDataSource(audioFile.absolutePath)
            player.prepare()
            player.setOnCompletionListener {
                isPlaying = false
                player.release()
                mediaPlayer = null
                onCompletion()
            }
            player.start()
            mediaPlayer = player
            isPlaying = true
            return true
        } catch (e: IOException) {
            Log.e("AudioRecorderHelper", "Playback error", e)
            isPlaying = false
            return false
        }
    }

    fun stopPlayback() {
        if (isPlaying) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) {
                // Ignored
            } finally {
                mediaPlayer = null
                isPlaying = false
            }
        }
    }

    fun deleteRecording(taskId: String): Boolean {
        stopPlayback()
        val audioDir = File(context.filesDir, "speaking_recordings")
        val audioFile = File(audioDir, "rec_${taskId}.m4a")
        return if (audioFile.exists()) {
            audioFile.delete()
        } else {
            false
        }
    }

    fun release() {
        stopRecording()
        stopPlayback()
    }
}
