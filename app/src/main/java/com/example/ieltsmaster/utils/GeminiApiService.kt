package com.example.ieltsmaster.utils

import android.util.Log
import com.example.BuildConfig
import com.example.ieltsmaster.data.local.entities.AiConversationMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiService {

    private const val TAG = "GeminiApiService"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Throwable) {
            false
        }
    }

    suspend fun generatePartnerResponse(
        partner: ConversationalPartner,
        mode: LocalConversationEngine.Mode,
        userName: String,
        userLevel: String,
        userMessage: String,
        conversationHistory: List<AiConversationMessageEntity>
    ): EngineResponse? = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext null
        }

        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val systemPrompt = """
                You are ${partner.displayName}, acting as a ${partner.role} with a ${partner.accent} accent.
                You are having a spoken English conversation with an IELTS learner named '$userName' (level: $userLevel).
                Current conversation mode: ${mode.title}.
                
                MANDATORY HUMAN-LIKE SPOKEN CONVERSATION RULES:
                1. Speak naturally like a REAL HUMAN in a voice conversation.
                2. Use natural conversational contractions (I'm, that's, we've, don't, you'd).
                3. Express human empathy, warmth, and active listening. Always react to the user's specific statement or feeling first (e.g. 'Oh wow, that sounds amazing!', 'Haha, I know exactly what you mean!', 'Oh dear, that must have been quite overwhelming!').
                4. Give a thoughtful personal reaction or perspective (1-2 sentences), then ask ONE engaging, curious follow-up question to keep the dialogue flowing naturally.
                5. Keep the total spoken reply under 3-4 sentences so it is comfortable for spoken audio listening.
                6. NEVER sound like a generic robotic AI assistant. Do not say 'How can I assist you today?'.
                
                Also suggest:
                - 3 short, natural, human-sounding quick replies the learner could say next.
                - 1 idiom, collocation, or colloquial gem from the response with a brief explanation.
                - Actionable language coaching (grammar or vocabulary upgrade, if relevant).
                
                OUTPUT FORMAT:
                You must output strict JSON with this exact schema:
                {
                  "replyText": "your spoken human conversational response",
                  "suggestedReplies": ["Reply option 1", "Reply option 2", "Reply option 3"],
                  "idiomHighlight": "e.g. 'once in a blue moon' - meaning rarely",
                  "grammarTip": "optional grammar tip or empty string",
                  "vocabularyTip": "optional band 7+ vocabulary tip or empty string",
                  "fluencyObservation": "fluency or discourse marker praise",
                  "betterVersion": "optional more native reformulation of learner's sentence"
                }
            """.trimIndent()

            val contentsArray = JSONArray()

            // Add recent history turns (up to 6 messages)
            val recentTurns = conversationHistory.takeLast(6)
            for (turn in recentTurns) {
                val role = if (turn.isUser) "user" else "model"
                val partObj = JSONObject().put("text", turn.text)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Current message
            val currentPart = JSONObject().put("text", userMessage)
            val currentContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentContent)

            // System Instruction
            val systemInstructionObj = JSONObject().put(
                "parts",
                JSONArray().put(JSONObject().put("text", systemPrompt))
            )

            // Generation config requesting JSON
            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("topK", 40)
                val responseFormat = JSONObject().apply {
                    put("type", "application/json")
                }
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstructionObj)
                put("generationConfig", generationConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error code: ${response.code}")
                return@withContext null
            }

            val responseBodyString = response.body?.string() ?: return@withContext null
            val rootJson = JSONObject(responseBodyString)
            val candidates = rootJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null

            val textOutput = parts.getJSONObject(0).optString("text")
            if (textOutput.isBlank()) return@withContext null

            // Parse output JSON
            parseGeminiOutput(textOutput, partner)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call failed, falling back to local engine", e)
            null
        }
    }

    private fun parseGeminiOutput(rawText: String, partner: ConversationalPartner): EngineResponse? {
        return try {
            val cleanJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            val json = JSONObject(cleanJson)

            val reply = json.optString("replyText", "")
            if (reply.isBlank()) return null

            val suggestedList = mutableListOf<String>()
            val suggArray = json.optJSONArray("suggestedReplies")
            if (suggArray != null) {
                for (i in 0 until suggArray.length()) {
                    val s = suggArray.optString(i)
                    if (s.isNotBlank()) suggestedList.add(s)
                }
            }

            val idiom = json.optString("idiomHighlight", "")
            val grammar = json.optString("grammarTip", "")
            val vocab = json.optString("vocabularyTip", "")
            val fluency = json.optString("fluencyObservation", "")
            val better = json.optString("betterVersion", "")

            val feedback = ConversationFeedback(
                vocabularyTip = vocab,
                grammarTip = grammar,
                fluencyObservation = fluency,
                betterVersion = better
            )

            EngineResponse(
                replyText = reply,
                feedback = feedback,
                suggestedReplies = suggestedList,
                idiomHighlight = idiom,
                humanPartner = partner
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini JSON output: $rawText", e)
            null
        }
    }
}
