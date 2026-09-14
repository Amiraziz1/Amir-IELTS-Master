package com.example.ieltsmaster.utils

enum class ConversationalPartner(
    val id: String,
    val displayName: String,
    val title: String,
    val role: String,
    val accent: String,
    val avatarEmoji: String,
    val colorHex: Long,
    val speechPitch: Float,
    val speechRate: Float,
    val description: String,
    val initialGreeting: String
) {
    EMMA(
        id = "EMMA",
        displayName = "Emma",
        title = "Emma • London, UK",
        role = "Friendly British Conversationalist",
        accent = "British (Warm & Casual)",
        avatarEmoji = "👩🏼‍💼",
        colorHex = 0xFFE91E63,
        speechPitch = 1.05f,
        speechRate = 0.95f,
        description = "Chat about daily life, feelings, hobbies, food, and culture with British conversational charm.",
        initialGreeting = "Hi there! I'm Emma. It is so lovely to meet you! How has your day been treating you so far?"
    ),
    DAVID(
        id = "DAVID",
        displayName = "Examiner David",
        title = "David • Cambridge, UK",
        role = "Official IELTS Senior Examiner",
        accent = "British (Polite & Precise)",
        avatarEmoji = "👨🏻‍🏫",
        colorHex = 0xFF1976D2,
        speechPitch = 0.92f,
        speechRate = 0.92f,
        description = "Realistic IELTS Speaking Parts 1, 2, and 3 interviews with probing questions and band guidance.",
        initialGreeting = "Good day. I am Examiner David. Welcome to our speaking assessment session. Shall we begin with your background and where you are from?"
    ),
    ALEX(
        id = "ALEX",
        displayName = "Coach Alex",
        title = "Alex • Fluency Coach",
        role = "Confidence & Idioms Mentor",
        accent = "American (Energetic & Modern)",
        avatarEmoji = "🧑🏽‍💻",
        colorHex = 0xFF4CAF50,
        speechPitch = 1.0f,
        speechRate = 1.0f,
        description = "Empowering coach who builds your confidence, teaches native idioms, and eliminates hesitations.",
        initialGreeting = "Hey! Coach Alex here. You are taking a huge step towards English fluency today. What is one topic you really want to get confident talking about?"
    ),
    MAYA(
        id = "MAYA",
        displayName = "Maya",
        title = "Maya • Oxford University",
        role = "Academic Discussion Peer",
        accent = "Academic (Thoughtful & Engaging)",
        avatarEmoji = "👩🏻‍🔬",
        colorHex = 0xFF9C27B0,
        speechPitch = 1.02f,
        speechRate = 0.94f,
        description = "Discuss intriguing topics: AI, psychology, society, philosophy, and global trends.",
        initialGreeting = "Hello! I am Maya. I love exploring fascinating ideas and global perspectives. What is a topic or news story that has caught your attention lately?"
    );

    companion object {
        fun fromId(id: String): ConversationalPartner {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: EMMA
        }
    }
}
