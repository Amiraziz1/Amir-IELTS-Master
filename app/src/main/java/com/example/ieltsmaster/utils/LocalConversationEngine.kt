package com.example.ieltsmaster.utils

import com.example.ieltsmaster.data.local.entities.AiConversationMessageEntity
import java.util.Locale

data class ConversationFeedback(
    val vocabularyTip: String = "",
    val grammarTip: String = "",
    val fluencyObservation: String = "",
    val betterVersion: String = ""
)

data class EngineResponse(
    val replyText: String,
    val feedback: ConversationFeedback? = null,
    val suggestedReplies: List<String> = emptyList(),
    val idiomHighlight: String = "",
    val humanPartner: ConversationalPartner = ConversationalPartner.EMMA
)

object LocalConversationEngine {

    enum class Mode(val id: String, val title: String, val subtitle: String, val promptHint: String) {
        BEGINNER("BEGINNER", "Beginner English", "Simple sentences & friendly encouragement", "Say hello or introduce yourself..."),
        DAILY("DAILY", "Daily Conversation", "Routines, hobbies, and casual talk", "Talk about your day, food, or hobbies..."),
        JOB_INTERVIEW("JOB_INTERVIEW", "Job Interview", "Professional workplace questions", "Answer interview questions professionally..."),
        TRAVEL("TRAVEL", "Travel & Tourism", "Airports, hotels, restaurants, directions", "Ask for directions, order food, or check in..."),
        UNIVERSITY("UNIVERSITY", "University & Academics", "Campus life, seminars, and group studies", "Discuss coursework, deadlines, or lectures..."),
        GENERAL("GENERAL", "General Discussion", "Technology, environment, and society", "Share your thoughts on modern issues..."),
        IELTS_PART_1("IELTS_PART_1", "IELTS Part 1", "Familiar topics: hometown, work, hobbies", "Answer like an IELTS Speaking Part 1 candidate..."),
        IELTS_PART_2("IELTS_PART_2", "IELTS Part 2", "2-minute cue card presentation practice", "Present your cue card topic in detail..."),
        IELTS_PART_3("IELTS_PART_3", "IELTS Part 3", "In-depth analytical & societal questions", "Provide balanced, analytical arguments...")
    }

    fun getInitialGreeting(
        mode: Mode,
        userName: String,
        targetBand: Float,
        partner: ConversationalPartner = ConversationalPartner.EMMA
    ): String {
        val name = if (userName.isBlank()) "there" else userName
        return when (partner) {
            ConversationalPartner.EMMA -> when (mode) {
                Mode.BEGINNER ->
                    "Hi $name! I'm Emma, so glad to meet you. Don't worry at all about making mistakes—we're just having a cozy chat. How has your day been feeling so far?"
                Mode.DAILY ->
                    "Hey $name! It's lovely to chat with you today. What have you been up to recently, or what's on your mind this week?"
                Mode.JOB_INTERVIEW ->
                    "Hello $name! Let's get you feeling completely natural and poised for job interviews. To start us off, could you tell me a little about yourself and what you enjoy doing professionally?"
                Mode.TRAVEL ->
                    "Hello there, fellow traveler! Welcome to our hotel in the city center. How can I help you settle in or point you towards the best local bakery?"
                Mode.UNIVERSITY ->
                    "Hi $name! Great to connect. University life can be quite a whirlwind. How is your term going, and what courses are keeping you busiest?"
                Mode.GENERAL ->
                    "Hey $name! I was just reflecting on how fast technology is moving these days. How do you feel about how AI and smart devices are changing our everyday habits?"
                Mode.IELTS_PART_1 ->
                    "Hello $name! Let's practice IELTS Part 1 together in a relaxed, conversational way. Tell me about where you're living right now—what do you like most about your neighborhood?"
                Mode.IELTS_PART_2 ->
                    "Hi $name! Here is your cue card: 'Describe a person who has made a profound impression on your life.' Think about who they are, how you met, and why they inspired you."
                Mode.IELTS_PART_3 ->
                    "Welcome $name. In Part 3, we dive into broader societal concepts. For example, some people believe modern urban living isolates people. What is your take on that?"
            }
            ConversationalPartner.DAVID -> when (mode) {
                Mode.IELTS_PART_1, Mode.BEGINNER, Mode.DAILY, Mode.GENERAL ->
                    "Good day, $name. I am Examiner David. In this speaking practice, we aim for fluency and natural coherence. Let us begin: Could you tell me a little about your hometown and what makes it distinctive?"
                Mode.IELTS_PART_2 ->
                    "Good day. For Speaking Part 2, here is your cue card: 'Describe an influential person who inspired your values or career.' Please present your topic clearly, covering who they are and their lasting impact."
                Mode.IELTS_PART_3 ->
                    "Let us proceed to Part 3 where we examine analytical questions. To what extent do you consider urbanization to be beneficial for a country's cultural identity?"
                else ->
                    "Good day $name. Welcome to our formal speaking assessment. Let us explore your communication goals and current level."
            }
            ConversationalPartner.ALEX -> when (mode) {
                Mode.DAILY, Mode.BEGINNER ->
                    "Hey $name! Coach Alex here. We're going to turn your spoken English into pure native confidence today! Tell me: what's one exciting thing that happened to you this week?"
                Mode.JOB_INTERVIEW ->
                    "Hey $name! Let's nail those interview answers with punchy idioms and clear impact. Kick us off: what is your absolute superpower at work?"
                else ->
                    "Welcome $name! Ready to push past your speaking comfort zone? What topic do you want to master today?"
            }
            ConversationalPartner.MAYA ->
                "Hello $name! I'm Maya from Oxford. I love dissecting intriguing questions and fresh ideas with fellow learners. What is something you recently read, watched, or thought about that sparked your curiosity?"
        }
    }

    fun processMessage(
        userInput: String,
        mode: Mode,
        userLevel: String,
        partner: ConversationalPartner = ConversationalPartner.EMMA,
        history: List<AiConversationMessageEntity> = emptyList()
    ): EngineResponse {
        val trimmed = userInput.trim()
        val lower = trimmed.lowercase(Locale.ENGLISH)

        // 1. Language coaching & feedback
        val feedback = analyzeUserInput(trimmed, lower)

        // 2. Formulate human response with empathy, personality, active reflection, and follow-up
        val (reply, suggestedReplies, idiomHighlight) = generateHumanDialogue(
            raw = trimmed,
            lower = lower,
            mode = mode,
            userLevel = userLevel,
            partner = partner,
            history = history
        )

        return EngineResponse(
            replyText = reply,
            feedback = feedback,
            suggestedReplies = suggestedReplies,
            idiomHighlight = idiomHighlight,
            humanPartner = partner
        )
    }

    private data class DialogueOutput(
        val reply: String,
        val suggestions: List<String>,
        val idiom: String
    )

    private fun generateHumanDialogue(
        raw: String,
        lower: String,
        mode: Mode,
        userLevel: String,
        partner: ConversationalPartner,
        history: List<AiConversationMessageEntity>
    ): DialogueOutput {
        // Detect conversational nuances: questions to AI, emotions, greetings, topics
        val isQuestionToAi = lower.contains("what about you") || lower.contains("and you") ||
                lower.contains("do you like") || lower.contains("have you ever") ||
                lower.contains("where are you") || lower.contains("who are you") ||
                lower.contains("what is your")

        val isGreeting = lower.startsWith("hello") || lower.startsWith("hi ") || lower == "hi" ||
                lower.startsWith("hey") || lower.contains("nice to meet")

        val isFatiguedOrStressed = lower.contains("tired") || lower.contains("exhausted") ||
                lower.contains("stressed") || lower.contains("nervous") || lower.contains("worried") ||
                lower.contains("hard day") || lower.contains("busy day")

        val isExcitedOrHappy = lower.contains("excited") || lower.contains("happy") ||
                lower.contains("great news") || lower.contains("wonderful") || lower.contains("amazing") ||
                lower.contains("passed") || lower.contains("celebrat")

        // 1. Questions to AI
        if (isQuestionToAi) {
            return handleQuestionsToAi(partner, lower)
        }

        // 2. Expressing fatigue, nervousness, or stress
        if (isFatiguedOrStressed) {
            return handleStressOrFatigue(partner)
        }

        // 3. Expressing high happiness or excitement
        if (isExcitedOrHappy) {
            return handleExcitement(partner)
        }

        // 4. Simple greeting
        if (isGreeting && raw.split("\\s+".toRegex()).size <= 4) {
            return handleGreeting(partner)
        }

        // 5. Topic-based human conversation flows
        return when {
            // Food & Cooking
            lower.contains("food") || lower.contains("eat") || lower.contains("cook") ||
                    lower.contains("restaurant") || lower.contains("pizza") || lower.contains("coffee") ||
                    lower.contains("tea") || lower.contains("dish") || lower.contains("dinner") ->
                handleFoodTopic(partner, lower)

            // Travel & Places
            lower.contains("travel") || lower.contains("trip") || lower.contains("visit") ||
                    lower.contains("country") || lower.contains("flight") || lower.contains("hotel") ||
                    lower.contains("beach") || lower.contains("mountain") || lower.contains("holiday") ->
                handleTravelTopic(partner, lower)

            // Hometown & Living
            lower.contains("hometown") || lower.contains("city") || lower.contains("neighborhood") ||
                    lower.contains("village") || lower.contains("born") || lower.contains("live in") ->
                handleHometownTopic(partner, lower)

            // Work, Jobs & Ambition
            lower.contains("work") || lower.contains("job") || lower.contains("company") ||
                    lower.contains("boss") || lower.contains("career") || lower.contains("colleague") ||
                    lower.contains("interview") || lower.contains("resume") ->
                handleCareerTopic(partner, lower)

            // Technology & AI
            lower.contains("tech") || lower.contains("ai") || lower.contains("artificial intelligence") ||
                    lower.contains("phone") || lower.contains("computer") || lower.contains("internet") ||
                    lower.contains("social media") || lower.contains("app") ->
                handleTechnologyTopic(partner, lower)

            // Movies, Music & Books
            lower.contains("movie") || lower.contains("film") || lower.contains("music") ||
                    lower.contains("song") || lower.contains("book") || lower.contains("read") ||
                    lower.contains("series") || lower.contains("watch") ->
                handleEntertainmentTopic(partner, lower)

            // Weekend & Free time
            lower.contains("weekend") || lower.contains("saturday") || lower.contains("sunday") ||
                    lower.contains("free time") || lower.contains("relax") || lower.contains("hobby") ->
                handleWeekendTopic(partner, lower)

            // Environment & Climate
            lower.contains("climate") || lower.contains("pollution") || lower.contains("environment") ||
                    lower.contains("green") || lower.contains("nature") || lower.contains("earth") ->
                handleEnvironmentTopic(partner, lower)

            // University & Studies
            lower.contains("study") || lower.contains("university") || lower.contains("exam") ||
                    lower.contains("college") || lower.contains("professor") || lower.contains("assignment") ->
                handleUniversityTopic(partner, lower)

            // IELTS Specific Cue Card or Part 2 / 3
            mode == Mode.IELTS_PART_2 ->
                handleIeltsPart2Response(partner, lower)

            mode == Mode.IELTS_PART_3 ->
                handleIeltsPart3Response(partner, lower)

            // Default contextual human conversation
            else -> handleDefaultHumanResponse(partner, raw, lower)
        }
    }

    private fun handleQuestionsToAi(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Oh, thanks for asking! If I'm being honest, I'm a huge fan of cozy independent coffee shops and reading historical fiction on rainy afternoons. What kind of spots or activities bring you the most peace when you need to recharge?"
            ConversationalPartner.DAVID ->
                "That is kind of you to inquire. In my years examining candidates, I have developed a deep appreciation for classical literature and brisk hill walks in the Lake District. In your own downtime, what activities help you maintain focus?"
            ConversationalPartner.ALEX ->
                "Haha, love that you turned the mic back on me! I'm completely hooked on marathon running and experimenting with spicy street food recipes. Tell me: what is one hobby that gets your adrenaline going?"
            ConversationalPartner.MAYA ->
                "I appreciate your curiosity! I spend quite a bit of my spare hours listening to philosophy podcasts and sketching architectural landmarks. Do you lean more towards artistic pursuits or analytical hobbies?"
        }
        val suggestions = listOf(
            "I love quiet outdoor walks in nature.",
            "To be honest, I'm really passionate about gaming and tech.",
            "Cooking comforting meals with my family is my favorite way to recharge."
        )
        return DialogueOutput(reply, suggestions, "'turn the tables' - to reverse a situation or question playfully")
    }

    private fun handleStressOrFatigue(partner: ConversationalPartner): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Oh bless you, I can hear how much has been on your plate! Please remember to take a gentle breath—you're doing brilliantly just by practicing today. What's one little thing that might help you unwind this evening?"
            ConversationalPartner.DAVID ->
                "Preparing for language assessments can indeed cause temporary fatigue. Pacing yourself is an essential strategy for endurance. What part of your preparation feels most demanding at this stage?"
            ConversationalPartner.ALEX ->
                "Hey, take a breath! We all hit walls sometimes. The secret isn't pushing until burnout—it's consistent, gentle reps. What was the toughest hurdle you faced today?"
            ConversationalPartner.MAYA ->
                "Mental fatigue is very real, especially when learning something as complex as a second language. Sometimes stepping away for a short stroll does wonders. What usually helps clear your mind?"
        }
        val suggestions = listOf(
            "Just getting a good night's sleep tonight.",
            "I get really anxious about speaking under timed exam conditions.",
            "Listening to calming music always helps me reset."
        )
        return DialogueOutput(reply, suggestions, "'hit a wall' - to reach a point of exhaustion or a temporary plateau")
    }

    private fun handleExcitement(partner: ConversationalPartner): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Haha, wow, your positive energy is absolutely wonderful! That has put such a smile on my face. How are you planning to celebrate, or who was the first person you shared the news with?"
            ConversationalPartner.DAVID ->
                "That is indeed splendid news. Success in language learning comes from sustained dedication, so you should take pride in this milestone. What do you attribute this breakthrough to?"
            ConversationalPartner.ALEX ->
                "YES! That is what I'm talking about! You worked hard for that, and you deserve to celebrate. What was the exact moment you realized you nailed it?"
            ConversationalPartner.MAYA ->
                "That sounds so fulfilling! Moments like this really reinforce the value of persistent effort. How has this accomplishment shifted your goals for the coming months?"
        }
        val suggestions = listOf(
            "I called my best friend right away to share the news!",
            "I treated myself to my favorite meal.",
            "It motivated me to aim for an even higher band score!"
        )
        return DialogueOutput(reply, suggestions, "'on cloud nine' - in a state of absolute joy and exhilaration")
    }

    private fun handleGreeting(partner: ConversationalPartner): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Hello there! It is so lovely to hear from you. The kettle is on, and I'm all ears! How has your morning or afternoon unfolded so far?"
            ConversationalPartner.DAVID ->
                "Good day. It is a pleasure to begin our session. To set a steady rhythm, how has your day been proceeding?"
            ConversationalPartner.ALEX ->
                "Hey there! Ready to crush some speaking practice today? Tell me what's on your agenda!"
            ConversationalPartner.MAYA ->
                "Hello! Delighted to connect. I hope your day is treating you kindly so far. What thoughts have been occupying your mind today?"
        }
        val suggestions = listOf(
            "It's been a busy day, but I'm ready to practice!",
            "Pretty relaxed so far, just catching up on some errands.",
            "I'm excited to practice some IELTS speaking topics with you today."
        )
        return DialogueOutput(reply, suggestions, "'all ears' - listening eagerly and with full attention")
    }

    private fun handleFoodTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Oh, you're making me hungry! There's something so magical about good food bringing people together. Do you lean more towards cooking something homemade from scratch, or do you love discovering hidden restaurant gems?"
            ConversationalPartner.DAVID ->
                "Gastronomy is a classic topic in IELTS Part 1 and Part 2. When describing cuisine, using sensory adjectives like 'aromatic', 'savory', or 'piquant' elevates your score. What is a signature traditional dish from your culture that you would recommend to an international visitor?"
            ConversationalPartner.ALEX ->
                "Boom! Food is the ultimate conversation starter. You can use great expressions like 'comfort food' or 'hits the spot'. What is your ultimate comfort dish when you're having a long day?"
            ConversationalPartner.MAYA ->
                "Culinary traditions are fascinating because they tell the social history of entire regions. How do you feel global fast food chains have influenced traditional dining habits in your community?"
        }
        val suggestions = listOf(
            "I much prefer authentic home-cooked meals made with fresh ingredients.",
            "To be honest, discovering quirky street food stalls is my passion!",
            "A traditional rice and herb dish from my country is truly unforgettable."
        )
        return DialogueOutput(reply, suggestions, "'hits the spot' - completely satisfies a craving or need")
    }

    private fun handleTravelTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Oh, wanderlust! Traveling really does open your mind in ways nothing else can. If you could pack your bags and teleport anywhere on Earth tomorrow, where would your heart take you?"
            ConversationalPartner.DAVID ->
                "Travel and tourism frequently feature in IELTS speaking assessments. Candidates who describe both cultural immersion and infrastructural observations score very favorably. Looking back, what was the most culturally enriching journey you have undertaken?"
            ConversationalPartner.ALEX ->
                "Yes! Travel stories are pure gold for speaking fluency. Think about vivid words like 'breathtaking landscapes' or 'off the beaten path'. What's a place you visited that completely exceeded your expectations?"
            ConversationalPartner.MAYA ->
                "Travel often forces us to rethink our assumptions about daily life and societal norms. Did you ever experience a cultural difference on a trip that fundamentally altered your perspective?"
        }
        val suggestions = listOf(
            "I would travel to the Swiss Alps to hike through the mountains.",
            "A trip to Japan completely opened my eyes to punctuality and hospitality.",
            "I prefer traveling off the beaten path rather than crowded tourist traps."
        )
        return DialogueOutput(reply, suggestions, "'off the beaten path' - isolated from frequented tourist routes")
    }

    private fun handleHometownTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Hometowns hold so many memories! Whether it's a bustling metropolis with vibrant night markets or a sleepy village where everyone knows your name, there's always a story. What is the one thing you miss most about your hometown when you're away?"
            ConversationalPartner.DAVID ->
                "In IELTS Part 1, hometown questions test your ability to balance descriptive vocabulary with personal sentiment. How has your hometown transformed over the past ten years in terms of public amenities and lifestyle?"
            ConversationalPartner.ALEX ->
                "Great topic! Use strong collocations like 'tight-knit community', 'bustling avenue', or 'peaceful retreat'. How would you describe the atmosphere of your hometown in three words?"
            ConversationalPartner.MAYA ->
                "Urban versus rural living presents such a fascinating sociological study. Do you think people who grow up in smaller towns have a different relationship with their neighbors compared to city dwellers?"
        }
        val suggestions = listOf(
            "It has a very tight-knit community where neighbors always support one another.",
            "Over the last decade, high-rise apartments and shopping malls have reshaped the skyline.",
            "What I miss most is the serene natural landscape and fresh air."
        )
        return DialogueOutput(reply, suggestions, "'tight-knit community' - a close group of people who care for each other")
    }

    private fun handleCareerTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Navigating professional life is such a journey! Finding that balance between doing meaningful work and having time for yourself is tricky. What is an aspect of your work or studies that genuinely brings you satisfaction?"
            ConversationalPartner.DAVID ->
                "Professional discourse requires clear lexical resource, such as 'career trajectory', 'interpersonal collaboration', and 'strategic decision-making'. What would you identify as your primary professional aspiration over the forthcoming five years?"
            ConversationalPartner.ALEX ->
                "Love talking career goals! Standout phrases like 'climbing the ladder', 'thinking outside the box', or 'team player' shine in interviews. What's a real-world problem you helped solve that you're super proud of?"
            ConversationalPartner.MAYA ->
                "The modern workplace is evolving rapidly with remote collaboration and asynchronous workflows. Do you feel remote work enhances productivity or dilutes interpersonal workplace bonds?"
        }
        val suggestions = listOf(
            "I really find satisfaction in solving complex problems collaboratively.",
            "In five years, I hope to lead innovative projects and mentor junior colleagues.",
            "Remote work offers great flexibility, but in-person bonding is still irreplaceable."
        )
        return DialogueOutput(reply, suggestions, "'think outside the box' - to formulate creative, unconventional ideas")
    }

    private fun handleTechnologyTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "It feels like new gadgets and AI breakthroughs appear every single week! On one hand it saves so much time, but on the other, screen time can be a real struggle. How do you manage your own digital detox when it gets overwhelming?"
            ConversationalPartner.DAVID ->
                "This is a quintessential IELTS Part 3 theme. When evaluating technological progress, providing a nuanced perspective with both merits and drawbacks demonstrates Band 8+ analytical maturity. Do you believe automation will inevitably exacerbate societal inequalities?"
            ConversationalPartner.ALEX ->
                "Tech is moving at warp speed! Great expressions to drop here are 'double-edged sword', 'game-changer', or 'cutting-edge'. Would you call AI a net positive or a double-edged sword for learners?"
            ConversationalPartner.MAYA ->
                "The philosophical implications of artificial intelligence are profound, especially regarding human creativity and critical thinking. If algorithms can compose music and write essays, what distinct role remains for human expression?"
        }
        val suggestions = listOf(
            "Technology is certainly a double-edged sword—convenient yet distracting.",
            "I try to establish tech-free zones, especially during dinner and before bed.",
            "Human empathy and authentic lived experiences cannot be replicated by algorithms."
        )
        return DialogueOutput(reply, suggestions, "'double-edged sword' - something that has both favorable and adverse outcomes")
    }

    private fun handleEntertainmentTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Oh, a great story whether in a book or on screen can completely transport you to another universe! What was the last movie, series, or novel that kept you glued to your seat until the very end?"
            ConversationalPartner.DAVID ->
                "Discussing arts and entertainment offers opportunities for rich evaluative language like 'compelling narrative', 'nuanced performance', or 'poignant thematic depth'. Do you believe cinema still serves as a reflection of societal values?"
            ConversationalPartner.ALEX ->
                "Yes! Storytelling is what connects humans across languages. Great phrases: 'page-turner', 'on the edge of my seat', 'mind-blowing plot twist'. What's a character you genuinely rooted for?"
            ConversationalPartner.MAYA ->
                "Literature and film often preserve cultural zeitgeists better than history books. Do you find yourself drawn more towards escapist fantasy or gritty, realistic narratives?"
        }
        val suggestions = listOf(
            "I recently watched a documentary that kept me on the edge of my seat.",
            "I'm drawn to realistic character studies that explore moral dilemmas.",
            "A gripping novel that was a total page-turner from chapter one."
        )
        return DialogueOutput(reply, suggestions, "'on the edge of one's seat' - in a state of suspense or high excitement")
    }

    private fun handleWeekendTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Weekends are sacred! Some people love a jam-packed itinerary full of markets and friends, while others just want a warm cup of tea and complete stillness. Which camp do you fall into when Saturday rolls around?"
            ConversationalPartner.DAVID ->
                "Leisure time allocation is another staple Part 1 topic. How do leisure habits in your society differ between the older generation and younger demographics?"
            ConversationalPartner.ALEX ->
                "Weekends are fuel for the week ahead! Great expressions: 'recharge my batteries', 'let my hair down', 'unwind after the daily grind'. What's your go-to ritual to recharge your batteries?"
            ConversationalPartner.MAYA ->
                "Sociologists often note the blurring boundary between leisure and productivity in our 'always-on' culture. Do you ever feel guilty when you take time to do absolutely nothing productive?"
        }
        val suggestions = listOf(
            "I love nothing more than recharging my batteries with a quiet book.",
            "I tend to fill my weekends with social outings and outdoor adventures.",
            "To be honest, I sometimes feel guilty relaxing because of work deadlines."
        )
        return DialogueOutput(reply, suggestions, "'recharge one's batteries' - to regain physical or mental energy through rest")
    }

    private fun handleEnvironmentTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Being in nature is so grounding, and seeing plastic waste or pollution breaks my heart. What is a small daily habit you've adopted to be a little kinder to the environment?"
            ConversationalPartner.DAVID ->
                "Environmental policy is a prominent IELTS Part 3 subject requiring balanced argumentative discourse. In your assessment, should governments implement stricter fiscal penalties on corporations to combat carbon emissions?"
            ConversationalPartner.ALEX ->
                "Green initiatives! Strong collocations: 'carbon footprint', 'renewable energy', 'sustainable practices'. Do you think individual efforts make a real dent, or does it all come down to big policy changes?"
            ConversationalPartner.MAYA ->
                "Environmental ethics asks us to consider our obligations to future generations who cannot advocate for themselves today. How do you perceive the balance between economic growth and environmental stewardship?"
        }
        val suggestions = listOf(
            "I consistently carry reusable bags and reduce single-use plastic.",
            "Stricter fiscal penalties on high-emission corporations are vital for real change.",
            "Individual actions matter, but systemic policy shifts drive the greatest impact."
        )
        return DialogueOutput(reply, suggestions, "'carbon footprint' - the total amount of greenhouse gases produced by actions")
    }

    private fun handleUniversityTopic(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "Studying can be exhilarating when you're passionate about the subject, but exam season is no joke! What's a study technique or ritual that keeps you sane when deadlines pile up?"
            ConversationalPartner.DAVID ->
                "Higher education equips individuals with analytical rigor and specialized methodologies. How effective do you consider current university curricula in preparing students for practical workforce challenges?"
            ConversationalPartner.ALEX ->
                "Ace those study sessions! Phrases like 'burning the midnight oil', 'cramming for exams', or 'hitting the books' fit naturally. Do you prefer studying solo or brainstorming in a study group?"
            ConversationalPartner.MAYA ->
                "Academia at Oxford taught me that questioning the premise is often more insightful than having an immediate answer. What is a controversial theory or topic in your field of study?"
        }
        val suggestions = listOf(
            "I prefer burning the midnight oil in the library with a quiet study group.",
            "University curricula should incorporate more hands-on practical internships.",
            "I use time-blocking and the Pomodoro technique to stay productive."
        )
        return DialogueOutput(reply, suggestions, "'burn the midnight oil' - to study or work late into the night")
    }

    private fun handleIeltsPart2Response(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.DAVID ->
                "Thank you very much for sharing that presentation. You maintained an even pace and structured your points effectively. To round off this cue card: In what ways do you believe this experience will continue shaping your outlook over the next five years?"
            ConversationalPartner.ALEX ->
                "Fantastic delivery! You expanded on the prompts with real energy. To make it even punchier, you can use linking phrases like 'Looking back in hindsight...' or 'What made this truly remarkable was...'. How would you summarize the core lesson you learned in one sentence?"
            else ->
                "That was such a compelling story to listen to! I felt like I was right there with you. How often do you reflect back on this memory when you're facing fresh challenges today?"
        }
        val suggestions = listOf(
            "Looking back in hindsight, it taught me the value of resilience.",
            "It will definitely guide my ethical decisions in my future career.",
            "I revisit this memory whenever I feel uncertain or hesitant."
        )
        return DialogueOutput(reply, suggestions, "'in hindsight' - understanding a situation only after it has happened")
    }

    private fun handleIeltsPart3Response(partner: ConversationalPartner, lower: String): DialogueOutput {
        val reply = when (partner) {
            ConversationalPartner.DAVID ->
                "That is a cogent argument with commendable analytical depth. However, alternative perspectives suggest that cultural homogenization is an inevitable consequence of globalization. How would you counter that position?"
            ConversationalPartner.MAYA ->
                "That touches on such a delicate balance. Some sociologists argue that localized traditions actually strengthen in reaction to global influences. Do you observe that resistance in your own community?"
            else ->
                "That is such a thought-provoking perspective! It makes you think about how different generations interpret progress. Do you think younger and older people in your country see eye-to-eye on this?"
        }
        val suggestions = listOf(
            "I would argue that modern communication allows traditions to be shared, not erased.",
            "Younger generations tend to embrace global trends, while elders preserve customs.",
            "It requires proactive educational policies to safeguard indigenous heritage."
        )
        return DialogueOutput(reply, suggestions, "'see eye to eye' - to agree fully with someone on a topic")
    }

    private fun handleDefaultHumanResponse(
        partner: ConversationalPartner,
        raw: String,
        lower: String
    ): DialogueOutput {
        // Extract key words from user message to mirror back like a real listener
        val words = raw.split("\\s+".toRegex()).filter { it.length > 3 }
        val keywordMirror = words.takeLast(2).joinToString(" ")

        val reply = when (partner) {
            ConversationalPartner.EMMA ->
                "That is such an interesting thought about $keywordMirror! I love the way you articulated that. Could you share a specific example or tell me what led you to that viewpoint?"
            ConversationalPartner.DAVID ->
                "Thank you for that observation. Developing your points with concrete justifications will bolster your Coherence and Cohesion score in IELTS. How would you elaborate on the broader significance of what you just mentioned?"
            ConversationalPartner.ALEX ->
                "Love the way you phrased that! You're speaking with so much more natural flow now. If you were explaining this to a close friend over coffee, what else would you add?"
            ConversationalPartner.MAYA ->
                "That opens up such a compelling dimension to explore regarding $keywordMirror. Have you noticed similar trends in other areas of life as well?"
        }
        val suggestions = listOf(
            "For instance, I experienced this firsthand just a few months ago.",
            "To be completely honest, it comes down to personal priorities.",
            "Could you explain your own take on this issue?"
        )
        return DialogueOutput(reply, suggestions, "'food for thought' - something that warrants serious, careful contemplation")
    }

    private fun analyzeUserInput(raw: String, lower: String): ConversationFeedback? {
        if (raw.length < 5) return null

        val words = raw.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size

        var grammarTip = ""
        var vocabTip = ""
        var betterVersion = ""

        // Common ESL grammar traps
        if (lower.contains("i am agree") || lower.contains("i'm agree")) {
            grammarTip = "Grammar trap: Say 'I agree' rather than 'I am agree' ('agree' is already a verb)."
            betterVersion = raw.replace("I am agree", "I agree", ignoreCase = true)
                .replace("I'm agree", "I agree", ignoreCase = true)
        } else if (lower.contains("he don't") || lower.contains("she don't") || lower.contains("it don't")) {
            grammarTip = "Subject-verb agreement: Use 'doesn't' with third-person singular subjects (he/she/it doesn't)."
            betterVersion = raw.replace("don't", "doesn't", ignoreCase = true)
        } else if (lower.contains("more better") || lower.contains("more easier")) {
            grammarTip = "Double comparative: Use 'much better' or simply 'easier' instead of 'more better'/'more easier'."
        } else if (lower.contains("in my opinion i think")) {
            grammarTip = "Redundancy: Use either 'In my opinion' OR 'I think', but not both together in one clause."
            betterVersion = raw.replace("In my opinion I think", "In my opinion,", ignoreCase = true)
        } else if (lower.contains("explain me")) {
            grammarTip = "Preposition rule: Say 'explain to me' rather than 'explain me'."
            betterVersion = raw.replace("explain me", "explain to me", ignoreCase = true)
        }

        // Vocabulary enhancements for Band 7+
        if (lower.contains("very good") || lower.contains("so good")) {
            vocabTip = "Band 7+ Lexical Upgrade: Replace 'very good' with 'exceptional', 'superb', or 'highly commendable'."
        } else if (lower.contains("a lot of")) {
            vocabTip = "Academic Phrasing: Elevate 'a lot of' to 'a substantial amount of' or 'numerous'."
        } else if (lower.contains("big problem")) {
            vocabTip = "Collocation Upgrade: Replace 'big problem' with 'pressing dilemma' or 'formidable challenge'."
        } else if (lower.contains("very important")) {
            vocabTip = "High-Band Synonym: Use 'paramount', 'crucial', or 'indispensable' instead of 'very important'."
        } else if (lower.contains("like it")) {
            vocabTip = "Idiomatic Alternative: Try 'I am particularly fond of...' or 'I have a real penchant for...'."
        }

        // Fluency Observation
        val hasDiscourseMarker = lower.contains("furthermore") || lower.contains("moreover") ||
                lower.contains("however") || lower.contains("consequently") ||
                lower.contains("on the other hand") || lower.contains("for instance") ||
                lower.contains("in addition") || lower.contains("to be honest") ||
                lower.contains("personally speaking")

        val fluencyObservation = when {
            hasDiscourseMarker -> "Great natural fluency! You used smooth conversational discourse markers."
            wordCount >= 18 -> "Rich elaboration ($wordCount words). Expanding with a quick concrete example will lock in Band 7.5+."
            wordCount in 8..17 -> "Good, balanced answer ($wordCount words). Smooth and comfortable spoken pace."
            else -> "A bit brief ($wordCount words). Try adding 'because...' or 'for example...' to expand naturally."
        }

        if (betterVersion.isBlank() && vocabTip.isNotBlank()) {
            betterVersion = raw
                .replace("very good", "exceptional", ignoreCase = true)
                .replace("a lot of", "a substantial number of", ignoreCase = true)
                .replace("big problem", "pressing dilemma", ignoreCase = true)
                .replace("very important", "paramount", ignoreCase = true)
        }

        return ConversationFeedback(
            vocabularyTip = vocabTip,
            grammarTip = grammarTip,
            fluencyObservation = fluencyObservation,
            betterVersion = betterVersion
        )
    }
}
