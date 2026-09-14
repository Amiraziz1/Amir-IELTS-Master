package com.example.ieltsmaster.data.local.database

import com.example.ieltsmaster.data.local.entities.*

object DataPreloader {

    fun getInitialProfile(): UserProfile {
        return UserProfile(
            id = 1,
            name = "Student",
            currentLevel = "Intermediate",
            targetBand = 7.5f,
            examType = "Academic",
            targetExamDate = "2026-11-20",
            dailyStudyTimeMinutes = 30,
            mainWeakness = "Speaking",
            isOnboarded = false,
            streakDays = 3,
            totalXp = 240,
            lastActiveDate = "2026-09-14"
        )
    }

    fun getInitialLessons(): List<LessonEntity> {
        return listOf(
            // Level A: Beginner Foundation
            LessonEntity(
                id = "beg_01",
                title = "Alphabet, Phonics & Basic Sounds",
                subtitle = "English pronunciation fundamentals & alphabet mastery",
                level = "Beginner",
                category = "FOUNDATION",
                estimatedMinutes = 10,
                orderIndex = 1,
                contentMarkdown = """
                    # The English Alphabet & Sounds
                    English has 26 letters and approximately 44 unique sounds (phonemes).
                    
                    ### Key Vowel Sounds
                    - **Short A**: /æ/ as in *cat*, *apple*
                    - **Long A**: /eɪ/ as in *make*, *train*
                    - **Short I**: /ɪ/ as in *sit*, *quick*
                    - **Long E**: /iː/ as in *seat*, *meet*
                    
                    ### Crucial Consonant Distinctions
                    - /b/ vs /v/: Notice your lips touch for **B** (*boat*), while top teeth touch bottom lip for **V** (*vote*).
                    - /p/ vs /f/: *pin* (bilabial plosive) vs *fin* (labiodental fricative).
                    
                    ### Practice Sentence:
                    "A quick brown fox jumps over the lazy dog." (Contains all 26 letters!)
                """.trimIndent()
            ),
            LessonEntity(
                id = "beg_02",
                title = "Greetings & Natural Introductions",
                subtitle = "Formal vs informal greetings in daily life",
                level = "Beginner",
                category = "FOUNDATION",
                estimatedMinutes = 10,
                orderIndex = 2,
                contentMarkdown = """
                    # Greetings & Introductions
                    
                    ### Formal Greetings
                    - "Good morning / afternoon / evening."
                    - "It is a pleasure to meet you."
                    - "How do you do?" (Standard formal reply: "How do you do?")
                    
                    ### Informal / Everyday Greetings
                    - "Hi / Hello there!"
                    - "How are you doing today?"
                    - "Nice to meet you!"
                    
                    ### Introducing Yourself
                    - "Hello, my name is Alex. I am a software designer from Montreal."
                    - "Nice to meet you! I have been living here for two years."
                """.trimIndent()
            ),
            LessonEntity(
                id = "beg_03",
                title = "Numbers, Time, Days & Months",
                subtitle = "Essential vocabulary for schedules and IELTS section 1",
                level = "Beginner",
                category = "FOUNDATION",
                estimatedMinutes = 12,
                orderIndex = 3,
                contentMarkdown = """
                    # Telling Time & Dates
                    Vital for IELTS Listening Section 1 (forms and appointments).
                    
                    ### Time Expressions
                    - 8:15 = "Quarter past eight" or "Eight fifteen"
                    - 8:30 = "Half past eight" or "Eight thirty"
                    - 8:45 = "Quarter to nine" or "Eight forty-five"
                    
                    ### Prepositions of Time:
                    - **AT**: specific times (*at 5 o'clock*, *at noon*, *at night*)
                    - **ON**: days & dates (*on Monday*, *on June 14th*, *on my birthday*)
                    - **IN**: months, years, seasons (*in July*, *in 2026*, *in summer*)
                """.trimIndent()
            ),

            // Level B: Elementary
            LessonEntity(
                id = "elem_01",
                title = "Present Simple vs Present Continuous",
                subtitle = "Habits, facts vs actions happening right now",
                level = "Elementary",
                category = "GRAMMAR",
                estimatedMinutes = 15,
                orderIndex = 4,
                contentMarkdown = """
                    # Present Simple vs Present Continuous
                    
                    ### Present Simple (Routine, General Truths)
                    - Form: Subject + Verb(s/es)
                    - "I commute to work by train every weekday."
                    - "Water boils at 100 degrees Celsius."
                    
                    ### Present Continuous (Happening now, Temporary trends)
                    - Form: Subject + am/is/are + Verb-ing
                    - "Global temperatures are rising rapidly."
                    - "Listen! The professor is explaining the IELTS format."
                    
                    ### Common Stative Verbs (Never in Continuous):
                    *Know, believe, understand, prefer, contain, belong, love, remember.*
                """.trimIndent()
            ),
            LessonEntity(
                id = "elem_02",
                title = "Articles: A, An, The & Zero Article",
                subtitle = "Mastering English articles for high accuracy",
                level = "Elementary",
                category = "GRAMMAR",
                estimatedMinutes = 15,
                orderIndex = 5,
                contentMarkdown = """
                    # The Complete Guide to English Articles
                    Article mistakes are one of the most common grammatical errors in IELTS writing!
                    
                    ### Indefinite Articles (A / An)
                    - Used with singular, countable nouns mentioned for the first time.
                    - "A" before consonant sounds: *a university* (/juː/), *a European city*.
                    - "An" before vowel sounds: *an hour* (/aʊər/), *an interesting study*.
                    
                    ### Definite Article (The)
                    - Used when both speaker and listener know the exact reference, or when unique.
                    - *The sun*, *The government*, *The data presented in the chart*.
                    
                    ### Zero Article (No article)
                    - Plural or uncountable nouns in a general sense: "Education is vital for societal development" (NOT *The education*).
                """.trimIndent()
            ),

            // Level C: Intermediate
            LessonEntity(
                id = "inter_01",
                title = "Present Perfect vs Past Simple",
                subtitle = "Unfinished time & life experience vs finished past events",
                level = "Intermediate",
                category = "GRAMMAR",
                estimatedMinutes = 15,
                orderIndex = 6,
                contentMarkdown = """
                    # Present Perfect vs Past Simple
                    Essential for IELTS Speaking Part 1 & Writing Task 1.
                    
                    ### Past Simple (Finished time period)
                    - Markers: *yesterday, in 2018, two days ago, last month*.
                    - "The population grew significantly between 2000 and 2010."
                    
                    ### Present Perfect (Connected to present or unfinished time)
                    - Markers: *already, yet, since, for, recently, so far*.
                    - "Governments have implemented stricter environmental policies recently."
                    - "I have studied English for three years." (Still studying now)
                """.trimIndent()
            ),
            LessonEntity(
                id = "inter_02",
                title = "Passive Voice in Academic English",
                subtitle = "Creating objective, formal tone for IELTS Academic Task 1 & 2",
                level = "Intermediate",
                category = "GRAMMAR",
                estimatedMinutes = 18,
                orderIndex = 7,
                contentMarkdown = """
                    # The Passive Voice
                    Academic writing requires an objective, neutral tone. Passive voice shifts focus from the person doing the action to the action itself.
                    
                    ### Structure:
                    Subject + Form of 'BE' + Past Participle (V3)
                    
                    ### Active vs Passive Comparison:
                    - *Active:* "Workers harvest the coffee beans by hand."
                    - *Passive:* "The coffee beans **are harvested** by hand." (Ideal for Task 1 Process diagrams!)
                    
                    ### Impersonal Passive for Academic Essays:
                    - "It is widely argued that..."
                    - "It has been demonstrated that regular exercise enhances cognitive clarity."
                """.trimIndent()
            ),

            // Level D: Upper Intermediate
            LessonEntity(
                id = "upper_01",
                title = "Conditionals: Real, Unreal & Mixed",
                subtitle = "First, Second, Third, and Mixed Conditionals for Band 7+ grammar range",
                level = "Upper Intermediate",
                category = "GRAMMAR",
                estimatedMinutes = 20,
                orderIndex = 8,
                contentMarkdown = """
                    # Advanced Conditionals
                    Examiners look for a variety of complex structures to award Band 7 and above in Grammatical Range and Accuracy.
                    
                    ### Zero Conditional (Scientific truths)
                    - *If + present, present*: "If ice melts, sea levels rise."
                    
                    ### First Conditional (Probable future)
                    - *If + present, will + infinitive*: "If governments subsidize renewable energy, fossil fuel dependency will decrease."
                    
                    ### Second Conditional (Hypothetical present/future)
                    - *If + past simple, would + infinitive*: "If I had more leisure time, I would learn classical piano."
                    
                    ### Third Conditional (Hypothetical past)
                    - *If + had + V3, would have + V3*: "If the authorities had warned the citizens earlier, fewer damages would have occurred."
                """.trimIndent()
            ),

            // Level E: Advanced & IELTS
            LessonEntity(
                id = "adv_01",
                title = "C1/C2 Academic Collocations & Lexical Resource",
                subtitle = "Transform simple phrases into high-band academic vocabulary",
                level = "Advanced",
                category = "VOCABULARY",
                estimatedMinutes = 20,
                orderIndex = 9,
                contentMarkdown = """
                    # Band 8+ Lexical Upgrades
                    
                    Avoid basic words by using precise academic collocations:
                    
                    - *Big problem* ➔ **Pressing issue / Grave concern**
                    - *Make something better* ➔ **Ameliorate / Mitigate adverse impacts**
                    - *Very important* ➔ **Of paramount significance / Indispensable**
                    - *Do an experiment* ➔ **Conduct empirical research**
                    - *Give money to* ➔ **Allocate fiscal resources to**
                    - *A lot of people think* ➔ **A prevailing school of thought posits that...**
                    
                    ### Example in IELTS Task 2:
                    "Instead of saying *'Air pollution is a big problem that government must solve'*, write:
                    **'Atmospheric contamination poses a grave concern that necessitates immediate governmental intervention.'**"
                """.trimIndent()
            ),
            LessonEntity(
                id = "spoken_01",
                title = "Everyday Conversational English to IELTS Fluency",
                subtitle = "Transition from textbook answers to natural, idiomatic spoken English",
                level = "Intermediate",
                category = "SPOKEN",
                estimatedMinutes = 15,
                orderIndex = 10,
                contentMarkdown = """
                    # Spoken English Simulation
                    
                    ### Moving Up the Speaking Ladder:
                    1. **Basic (Band 5):** "I like reading books. It is good."
                    2. **Natural (Band 6):** "I really enjoy reading fiction because it helps me relax after a long day."
                    3. **Fluent (Band 7+):** "I am an avid reader of historical fiction. What captivates me most is gaining insight into different historical epochs, which provides a welcome escape from my daily grind."
                    
                    ### Useful Discourse Markers:
                    - To buy time to think: *"Well, that's an intriguing question..."*
                    - To elaborate: *"To put it another way..."* or *"What I mean by that is..."*
                    - To balance views: *"On the flip side, though..."*
                """.trimIndent()
            ),
            LessonEntity(
                id = "pron_01",
                title = "Pronunciation, Intonation & Sentence Stress",
                subtitle = "Sound natural, avoid monotone delivery and master connected speech",
                level = "Intermediate",
                category = "PRONUNCIATION",
                estimatedMinutes = 15,
                orderIndex = 11,
                contentMarkdown = """
                    # Pronunciation & Connected Speech
                    Pronunciation accounts for 25% of your IELTS Speaking score.
                    
                    ### 1. Linking Sounds (Liaison)
                    When a word ends in a consonant sound and the next begins with a vowel:
                    - *Hold on* ➔ /həʊl-dɒn/
                    - *An apple* ➔ /ə-næpl/
                    
                    ### 2. Intonation (Music of English)
                    - **Falling intonation (↘)**: Definite statements, answers, closed questions.
                      *"I lived in Berlin for five years. ↘"*
                    - **Rising intonation (↗)**: Lists before the final item, checking understanding, polite requests.
                      *"I enjoy swimming ↗, cycling ↗, and jogging. ↘"*
                      
                    ### 3. Schwa /ə/
                    The most common sound in English! Unstressed syllables turn into /ə/:
                    - *About* (/əˈbaʊt/), *Problem* (/ˈprɒbləm/), *Banana* (/bəˈnɑːnə/).
                """.trimIndent()
            )
        )
    }

    fun getInitialVocabulary(): List<VocabularyWordEntity> {
        return listOf(
            VocabularyWordEntity(
                word = "Ameliorate",
                phonetic = "/əˈmiːliəreɪt/",
                partOfSpeech = "verb",
                meaning = "To make something bad or unsatisfactory better; to improve.",
                exampleSentence = "Subsidized public transit policies can ameliorate urban traffic congestion.",
                difficulty = "Advanced",
                topic = "Environment",
                synonyms = "Improve, alleviate, enhance, mitigate",
                antonyms = "Exacerbate, worsen, deteriorate",
                collocations = "Ameliorate conditions, ameliorate poverty, measures to ameliorate",
                ieltsRelevance = "Band 8+ Task 2 essay solution paragraphs",
                masteryStatus = "NEW"
            ),
            VocabularyWordEntity(
                word = "Ubiquitous",
                phonetic = "/juːˈbɪkwɪtəs/",
                partOfSpeech = "adjective",
                meaning = "Present, appearing, or found everywhere.",
                exampleSentence = "Smartphones and wireless internet have become ubiquitous in modern households.",
                difficulty = "Advanced",
                topic = "Technology",
                synonyms = "Omnipresent, pervasive, universal",
                antonyms = "Rare, scarce, isolated",
                collocations = "Ubiquitous influence, ubiquitous technology, become ubiquitous",
                ieltsRelevance = "Band 7.5+ Technology and Society essays",
                masteryStatus = "LEARNING"
            ),
            VocabularyWordEntity(
                word = "Detrimental",
                phonetic = "/ˌdɛtrɪˈmɛntl/",
                partOfSpeech = "adjective",
                meaning = "Tending to cause harm or damage.",
                exampleSentence = "Excessive screen time exerts a detrimental impact on adolescent sleep cycles.",
                difficulty = "Intermediate",
                topic = "Health",
                synonyms = "Harmful, adverse, damaging, deleterious",
                antonyms = "Beneficial, advantageous, constructive",
                collocations = "Detrimental effect on, highly detrimental, prove detrimental",
                ieltsRelevance = "High frequency in IELTS Writing & Speaking",
                masteryStatus = "KNOWN"
            ),
            VocabularyWordEntity(
                word = "Proliferation",
                phonetic = "/prəˌlɪfəˈreɪʃn/",
                partOfSpeech = "noun",
                meaning = "Rapid increase in the number or amount of something.",
                exampleSentence = "The proliferation of digital misinformation requires robust educational counter-measures.",
                difficulty = "Advanced",
                topic = "Media",
                synonyms = "Expansion, surge, escalation, multiplying",
                antonyms = "Reduction, decline, scarcity",
                collocations = "Proliferation of devices, rapid proliferation, prevent proliferation",
                ieltsRelevance = "Band 8+ Lexical Resource",
                masteryStatus = "NEW"
            ),
            VocabularyWordEntity(
                word = "Substantiate",
                phonetic = "/səbˈstænʃieɪt/",
                partOfSpeech = "verb",
                meaning = "To provide evidence to support or prove the truth of a claim.",
                exampleSentence = "Empirical data is imperative to substantiate the researcher's hypothesis.",
                difficulty = "Academic",
                topic = "Science",
                synonyms = "Validate, corroborate, verify, justify",
                antonyms = "Disprove, refute, contradict",
                collocations = "Substantiate claims, fail to substantiate, evidence to substantiate",
                ieltsRelevance = "Crucial for Academic Reading and Task 2 argument building",
                masteryStatus = "NEW"
            ),
            VocabularyWordEntity(
                word = "Pragmatic",
                phonetic = "/præɡˈmætɪk/",
                partOfSpeech = "adjective",
                meaning = "Dealing with things sensibly and realistically based on practical rather than theoretical considerations.",
                exampleSentence = "Policymakers must adopt a pragmatic approach to municipal waste management.",
                difficulty = "Advanced",
                topic = "Government",
                synonyms = "Practical, realistic, utilitarian, sensible",
                antonyms = "Idealistic, impractical, theoretical",
                collocations = "Pragmatic approach, pragmatic solution, pragmatic measures",
                ieltsRelevance = "Band 7+ Speaking Part 3 & Writing Task 2",
                masteryStatus = "REVIEW"
            ),
            VocabularyWordEntity(
                word = "Sustainable",
                phonetic = "/səˈsteɪnəbl/",
                partOfSpeech = "adjective",
                meaning = "Able to be maintained at a certain rate or level without exhausting natural resources.",
                exampleSentence = "Transitioning to renewable energy sources is vital for sustainable economic growth.",
                difficulty = "Intermediate",
                topic = "Environment",
                synonyms = "Renewable, viable, eco-friendly, maintainable",
                antonyms = "Unsustainable, depleting, fleeting",
                collocations = "Sustainable development, sustainable practices, ecologically sustainable",
                ieltsRelevance = "Top 10 most common IELTS essay vocabulary words",
                masteryStatus = "KNOWN"
            ),
            VocabularyWordEntity(
                word = "Compelling",
                phonetic = "/kəmˈpɛlɪŋ/",
                partOfSpeech = "adjective",
                meaning = "Evoking interest, attention, or admiration in a powerfully irresistible way; convincing.",
                exampleSentence = "There is compelling evidence that bilingualism stimulates cognitive development.",
                difficulty = "Advanced",
                topic = "Education",
                synonyms = "Convincing, persuasive, forceful, captivating",
                antonyms = "Unconvincing, weak, dull",
                collocations = "Compelling evidence, compelling argument, compelling reason",
                ieltsRelevance = "Used for strong thesis statements and supporting claims",
                masteryStatus = "LEARNING"
            ),
            VocabularyWordEntity(
                word = "Exacerbate",
                phonetic = "/ɪɡˈzæsərbeɪt/",
                partOfSpeech = "verb",
                meaning = "To make a problem, bad situation, or negative feeling worse.",
                exampleSentence = "Rising industrial emissions exacerbate the severity of global climate phenomena.",
                difficulty = "Advanced",
                topic = "Environment",
                synonyms = "Aggravate, intensify, inflame, worsen",
                antonyms = "Alleviate, soothe, ameliorate",
                collocations = "Exacerbate the problem, exacerbate tensions, further exacerbate",
                ieltsRelevance = "Band 7.5+ problem-solution essays",
                masteryStatus = "NEW"
            ),
            VocabularyWordEntity(
                word = "Equitable",
                phonetic = "/ˈɛkwɪtəbl/",
                partOfSpeech = "adjective",
                meaning = "Fair and impartial; treating everyone justly.",
                exampleSentence = "Ensuring equitable distribution of healthcare resources is a cardinal duty of the state.",
                difficulty = "Academic",
                topic = "Society",
                synonyms = "Fair, just, egalitarian, unbiased",
                antonyms = "Inequitable, discriminatory, partial",
                collocations = "Equitable access, equitable society, equitable distribution",
                ieltsRelevance = "Society & Government Task 2 essays",
                masteryStatus = "NEW"
            )
        )
    }

    fun getInitialGrammarTopics(): List<GrammarTopicEntity> {
        return listOf(
            GrammarTopicEntity(
                topicId = "tenses_pres_simple",
                category = "Tenses",
                title = "Present Simple & Continuous",
                level = "Elementary",
                explanation = "Present simple describes habitual actions and timeless truths. Present continuous describes actions occurring at the moment of speaking or ongoing temporary trends.",
                formulaOrStructure = "Simple: S + V(s/es) | Continuous: S + is/am/are + V-ing",
                examplesJson = "[\"The sun rises in the east.\", \"She is currently conducting a field survey for her thesis.\"]",
                commonMistakesJson = "[\"Incorrect: She is knowing the answer. (know is stative, use knows)\", \"Incorrect: I work here since 2020. (use have worked)\"]",
                isMastered = true
            ),
            GrammarTopicEntity(
                topicId = "tenses_perf_past",
                category = "Tenses",
                title = "Present Perfect vs Past Simple",
                level = "Intermediate",
                explanation = "Past Simple designates a completed event in a finished time period. Present Perfect denotes an action with present relevance or within an unfinished timeframe.",
                formulaOrStructure = "Past Simple: S + V2 | Present Perfect: S + have/has + V3",
                examplesJson = "[\"The company opened its first overseas branch in 2012.\", \"The company has expanded significantly over the past decade.\"]",
                commonMistakesJson = "[\"Incorrect: I have seen him yesterday. (yesterday is finished past, use saw)\", \"Incorrect: He lived here for 5 years and still lives here. (use has lived)\"]",
                isMastered = false
            ),
            GrammarTopicEntity(
                topicId = "cond_second_third",
                category = "Conditionals",
                title = "Second, Third & Mixed Conditionals",
                level = "Upper Intermediate",
                explanation = "Second conditional explores unreal present/future possibilities. Third conditional explores counterfactual past events. Mixed conditionals link past actions to present results.",
                formulaOrStructure = "2nd: If + past simple, would + bare inf | 3rd: If + had + V3, would have + V3 | Mixed: If + had + V3, would + inf",
                examplesJson = "[\"If renewable energy were cheaper, more households would adopt it.\", \"If the city had invested in drainage, the flash floods would have been avoided.\", \"If I had studied harder in high school, I would be a doctor today.\"]",
                commonMistakesJson = "[\"Incorrect: If I would know, I would tell you. (use If I knew)\", \"Incorrect: If he had arrived earlier, he would caught the flight. (use would have caught)\"]",
                isMastered = false
            ),
            GrammarTopicEntity(
                topicId = "passive_academic",
                category = "Passive Voice",
                title = "Academic Passive & Impersonal Structures",
                level = "Intermediate",
                explanation = "Used in IELTS Academic Writing to emphasize the research, experiment, or trend rather than individual subjective actors.",
                formulaOrStructure = "It + is/has been + past participle (believed/argued/demonstrated) + that + clause",
                examplesJson = "[\"It is widely accepted that bilingual education confers cognitive benefits.\", \"The water is heated to 100°C before the filtration process is initiated.\"]",
                commonMistakesJson = "[\"Incorrect: It is argue that... (use argued)\", \"Incorrect: The research was conducted by myself and resulted in success. (Overuse of personal pronouns)\"]",
                isMastered = false
            ),
            GrammarTopicEntity(
                topicId = "rel_clauses",
                category = "Relative Clauses",
                title = "Defining vs Non-Defining Relative Clauses",
                level = "Intermediate",
                explanation = "Defining clauses provide essential identity information without commas. Non-defining clauses offer extra non-essential details surrounded by commas (never using 'that').",
                formulaOrStructure = "Defining: Noun + who/which/that + clause | Non-defining: Noun, who/which + clause, ...",
                examplesJson = "[\"Individuals who exercise regularly tend to experience lower stress levels.\", \"Solar energy, which is completely carbon-neutral, has surged in popularity.\"]",
                commonMistakesJson = "[\"Incorrect: Solar energy, that is carbon-neutral, has surged... (never use 'that' in non-defining clauses with commas)\"]",
                isMastered = false
            )
        )
    }

    fun getInitialQuestions(): List<QuestionEntity> {
        return listOf(
            QuestionEntity(
                questionId = "q_list_01",
                moduleOrSkill = "LISTENING",
                topicOrLessonId = "listening_section1",
                questionType = "MULTIPLE_CHOICE",
                questionText = "What is the primary reason the speaker enrolled in the evening academic writing seminar?",
                passageOrScript = "Speaker: 'I originally thought about brushing up on my conversational fluency, but my department supervisor highlighted that my research proposal lacked formal academic cohesion. So I signed up specifically to refine my journal submission style.'",
                optionsJson = "[\"To improve general conversational fluency\", \"To fulfill a mandatory university course requirement\", \"To refine formal academic writing for a journal proposal\", \"To meet new colleagues in the department\"]",
                correctAnswer = "To refine formal academic writing for a journal proposal",
                explanation = "The speaker notes that although they initially thought of conversation, their supervisor pointed out issues in formal cohesion, leading them to enroll specifically to refine their journal submission.",
                targetBand = 7.0f
            ),
            QuestionEntity(
                questionId = "q_read_01",
                moduleOrSkill = "READING",
                topicOrLessonId = "reading_academic",
                questionType = "TRUE_FALSE_NOT_GIVEN",
                questionText = "The initial trials of the wave-energy generator proved commercially viable within the first six months.",
                passageOrScript = "Passage Excerpt: 'Although preliminary thermodynamic models had predicted immediate cost parity with wind turbines, the initial sea trials off the coast of Scotland encountered severe turbine wear. Operating costs in the inaugural half-year outpaced revenue by nearly forty percent, deferring commercial self-sufficiency until redesigned ceramic bearings were fitted two years later.'",
                optionsJson = "[\"TRUE\", \"FALSE\", \"NOT GIVEN\"]",
                correctAnswer = "FALSE",
                explanation = "The passage states that operating costs outpaced revenue by 40% in the inaugural half-year and commercial viability was deferred for two years. Therefore, the statement is directly contradicted (FALSE).",
                targetBand = 7.5f
            ),
            QuestionEntity(
                questionId = "q_read_02",
                moduleOrSkill = "READING",
                topicOrLessonId = "reading_academic",
                questionType = "MULTIPLE_CHOICE",
                questionText = "According to paragraph 2, why did researchers substitute titanium components with composite carbon fiber?",
                passageOrScript = "Passage Excerpt: 'While titanium exhibited remarkable resistance to corrosive saltwater, its prohibitive weight curtailed the buoy's rotational frequency. By substituting titanium with lightweight composite carbon fiber, the engineering team quadrupled the oscillation velocity while trimming fabrication expenses.'",
                optionsJson = "[\"To enhance saltwater corrosion resistance\", \"To decrease weight and amplify rotational oscillation speed\", \"To comply with international environmental maritime standards\", \"To extend the battery longevity of monitoring sensors\"]",
                correctAnswer = "To decrease weight and amplify rotational oscillation speed",
                explanation = "The passage states that titanium was too heavy, which curtailed the rotational frequency; substituting with carbon fiber quadrupled oscillation velocity while cutting costs.",
                targetBand = 7.5f
            ),
            QuestionEntity(
                questionId = "q_gram_01",
                moduleOrSkill = "GRAMMAR",
                topicOrLessonId = "cond_second_third",
                questionType = "MULTIPLE_CHOICE",
                questionText = "Select the grammatically accurate sentence exhibiting third conditional structure:",
                passageOrScript = "",
                optionsJson = "[\"If the government invested earlier, urban traffic will be resolved.\", \"If the authorities had allocated fiscal subsidies, the green transit system would have expanded faster.\", \"If they would have studied the demographic data, they might avoid the deficit.\", \"If the city had been more walkable, people live healthier.\"]",
                correctAnswer = "If the authorities had allocated fiscal subsidies, the green transit system would have expanded faster.",
                explanation = "Third conditional follows 'If + past perfect (had allocated), would have + past participle (would have expanded)'. The other choices confuse conditional forms or incorrectly insert 'would have' into the if-clause.",
                targetBand = 7.0f
            ),
            QuestionEntity(
                questionId = "q_vocab_01",
                moduleOrSkill = "VOCABULARY",
                topicOrLessonId = "c1_lexical_resource",
                questionType = "FILL_BLANK",
                questionText = "Choose the most academically appropriate word to complete: 'Prompt urban planning policies can _______ the adverse effects of urban heat islands.'",
                passageOrScript = "",
                optionsJson = "[\"ameliorate\", \"deteriorate\", \"exacerbate\", \"substantiate\"]",
                correctAnswer = "ameliorate",
                explanation = "'Ameliorate' means to improve or make better a negative situation. 'Exacerbate' and 'deteriorate' would worsen it, while 'substantiate' means to prove with evidence.",
                targetBand = 8.0f
            )
        )
    }

    fun getInitialSpeakingTasks(): List<SpeakingTaskEntity> {
        return listOf(
            SpeakingTaskEntity(
                taskId = "spk_p1_01",
                part = 1,
                topic = "Hometown & Neighborhood",
                prompt = "Let's talk about where you live. Can you describe your hometown or the neighborhood you currently reside in?",
                bulletPointsJson = "[\"Where is it located?\", \"What do you find most appealing about it?\", \"Has it changed much in recent years?\", \"Do you plan on remaining there long-term?\"]",
                prepTimeSeconds = 15,
                speakTimeSeconds = 60,
                modelAnswer = "I hail from a coastal city renowned for its vibrant maritime commerce and temperate climate. What appeals to me most is the harmonious balance between modern metropolitan amenities and serene waterfront promenades. Over the past decade, rapid infrastructure projects have transformed the skyline, introducing green parks and cycle paths. While I cherish its distinctive charm, I anticipate relocating overseas in the near future to pursue advanced postgraduate research.",
                vocabularyTips = "Coastal city, temperate climate, harmonious balance, metropolitan amenities, waterfront promenades, rapid infrastructure.",
                bandCriteriaTips = "Aim for fluent flow without long unnatural hesitations. Use descriptive adjectives and complex clauses naturally."
            ),
            SpeakingTaskEntity(
                taskId = "spk_p2_01",
                part = 2,
                topic = "An Influential Teacher or Mentor",
                prompt = "Describe a teacher or mentor who has had a significant positive influence on your education or personal development.",
                bulletPointsJson = "[\"Who this person was\", \"What subject or skill they guided you in\", \"How they conveyed concepts differently from others\", \"And explain why their impact remains meaningful to you today\"]",
                prepTimeSeconds = 60,
                speakTimeSeconds = 120,
                modelAnswer = "I would like to speak about my high school chemistry instructor, Mr. Henderson. He had an uncanny knack for demystifying abstract scientific principles. Rather than relying on rote memorization from outdated textbooks, he orchestrated hands-on laboratory experiments and challenged us to question prevailing assumptions. What set him apart was his unwavering belief in our innate curiosity. His guidance not only ignited my passion for empirical science but instilled a lifelong dedication to critical inquiry.",
                vocabularyTips = "Uncanny knack, demystifying abstract principles, rote memorization, hands-on laboratory experiments, prevailing assumptions, empirical science, critical inquiry.",
                bandCriteriaTips = "Structure your talk chronologically: Introduction ➔ Detailed background ➔ Method/Action ➔ Lasting personal reflection. Speak fluently across the full 2 minutes."
            ),
            SpeakingTaskEntity(
                taskId = "spk_p3_01",
                part = 3,
                topic = "The Evolution of Modern Education & Technology",
                prompt = "How has digital technology transformed the traditional relationship between educators and students?",
                bulletPointsJson = "[\"Shift from instructor-led to facilitator-led learning\", \"Ubiquity of online resources versus verified expertise\", \"The future role of human empathy in computerized classrooms\"]",
                prepTimeSeconds = 10,
                speakTimeSeconds = 90,
                modelAnswer = "Undoubtedly, the proliferation of digital platforms has fundamentally shifted the pedagogical landscape. In bygone decades, the teacher was viewed as the sole repository of knowledge. Today, with open-access repositories and interactive learning modules, educators have evolved into facilitators who guide students through information appraisal and synthesis. However, machines cannot replicate emotional intelligence, moral mentorship, or spontaneous Socratic debate. Hence, the human element will remain indispensable.",
                vocabularyTips = "Pedagogical landscape, sole repository of knowledge, information appraisal and synthesis, Socratic debate, indispensable human element.",
                bandCriteriaTips = "Give extended abstract opinions with reasoned justification, balanced counterarguments, and speculative future predictions."
            )
        )
    }

    fun getInitialWritingTasks(): List<WritingTaskEntity> {
        return listOf(
            WritingTaskEntity(
                taskId = "wri_acad_t1_01",
                taskType = "ACADEMIC_TASK_1",
                prompt = "The line graph below illustrates electricity generation by source (Solar, Wind, Hydro, and Coal) in a European nation between 2000 and 2025. Summarise the information by selecting and reporting the main features, and make comparisons where relevant. Write at least 150 words.",
                scenarioOrDataDescription = "Data Summary: In 2000, Coal dominated at 65% of total output, while Hydro stood at 25%, Wind at 8%, and Solar at a negligible 2%. By 2025, Coal plummeted to 18%, while Wind surged to become the primary contributor at 38%, Solar climbed to 24%, and Hydro stabilized at 20%.",
                sampleBand9Answer = "The line graph delineates the trajectory of four distinct electricity generation modalities in a designated European nation from 2000 through 2025.\n\nOverall, the period witnessed a dramatic transition from conventional fossil fuels toward sustainable alternatives. Notably, wind energy experienced exponential growth to emerge as the predominant power source, while coal suffered a steep and continuous downturn.\n\nAt the onset of the millennium, coal was unequivocally the dominant contributor, accounting for roughly 65% of national power generation. In stark contrast, renewable resources represented modest shares, with hydro, wind, and solar supplying 25%, 8%, and a marginal 2% respectively.\n\nOver the subsequent quarter-century, coal output contracted sharply, plummeting to a mere 18% by 2025. Conversely, wind power embarked on a steady upward ascent, eclipsing hydro by 2012 and peaking at 38% in the terminal year. Solar power followed a comparable trajectory, registering substantial gains to finish at 24%. Meanwhile, hydroelectric generation fluctuated moderately within a narrow band before consolidating at 20%.",
                keyVocabulary = "Delineates the trajectory, distinct electricity modalities, dramatic transition, predominant power source, onset of the millennium, in stark contrast, plunged sharply, steady upward ascent, eclipsing.",
                structureChecklist = "Introduction (paraphrase prompt) | Overview (2 main high-level trends) | Detail Paragraph 1 (Coal & Hydro) | Detail Paragraph 2 (Wind & Solar comparisons)"
            ),
            WritingTaskEntity(
                taskId = "wri_acad_t2_01",
                taskType = "ACADEMIC_TASK_2",
                prompt = "Some educators argue that artificial intelligence tools should be integrated into university curricula, while others fear they will undermine independent critical thinking. Discuss both views and give your own opinion. Write at least 250 words.",
                scenarioOrDataDescription = "Essay Type: Discussion Essay (Discuss both views & give your own reasoned opinion). Target Band: 8.5 - 9.0.",
                sampleBand9Answer = "The rapid advent of generative artificial intelligence has sparked intense debate regarding its role in tertiary education. While skeptics contend that cognitive reliance on automated systems degrades analytical acumen, proponents argue that strategic integration fosters essential modern technological literacy. In my estimation, when deployed judiciously under rigorous academic supervision, AI tools serve as potent cognitive catalysts rather than impediments to intellect.\n\nOn the one hand, apprehensions regarding intellectual complacency are not without merit. If students routinely outsource fundamental research tasks, essay structuring, and mathematical proofs to automated algorithms, their capacity for deep cognitive synthesis may atrophy. Empirical cognitive studies confirm that meaningful comprehension emerges from grappling directly with intellectual friction. Over-dependence on pre-packaged computational outputs risks yielding graduates who lack genuine problem-solving originality.\n\nOn the other hand, proponents emphasize that universities must mirror real-world professional landscapes, where AI literacy has become indispensable. Rather than displacing critical faculties, intelligent software can expedite preliminary literature reviews and simulate intricate statistical models, thereby freeing students to focus on high-order hypothesis evaluation and ethical discourse. Furthermore, mastering the art of prompt formulation and critical output verification actively cultivates nuanced skepticism.\n\nIn conclusion, while unchecked automation could indeed blunt raw analytical skills, outright prohibition is both impractical and counterproductive. Higher education institutions should instead establish structured pedagogical frameworks that harness AI as an intellectual sparring partner, ultimately elevating human critical thought.",
                keyVocabulary = "Tertiary education, cognitive reliance, analytical acumen, potent cognitive catalysts, intellectual complacency, cognitive synthesis, intellectual friction, pedagogical frameworks, intellectual sparring partner.",
                structureChecklist = "1. Introduction with clear thesis | 2. Body Paragraph 1 (Critique of over-reliance) | 3. Body Paragraph 2 (Benefits of strategic facilitation) | 4. Balanced Conclusion with personal stance"
            )
        )
    }

    fun getInitialMockTests(): List<MockTestEntity> {
        return listOf(
            MockTestEntity(
                testId = "mock_acad_01",
                title = "IELTS Academic Full Simulation 01",
                testType = "Academic",
                durationMinutes = 60,
                totalQuestions = 40,
                description = "Authentic full-length practice session covering Listening Section, Academic Reading Passages, and interactive evaluation modules.",
                targetBandLevel = "Band 6.5 - 8.5"
            ),
            MockTestEntity(
                testId = "mock_gen_01",
                title = "IELTS General Training Practice 01",
                testType = "General Training",
                durationMinutes = 60,
                totalQuestions = 40,
                description = "Workplace notices, daily life scenarios, and formal letter writing prompts calibrated for General Training candidates.",
                targetBandLevel = "Band 6.0 - 8.0"
            ),
            MockTestEntity(
                testId = "mock_band7_booster",
                title = "Band 7.5+ High-Scorer Challenge",
                testType = "Skill Focus",
                durationMinutes = 45,
                totalQuestions = 30,
                description = "Targeted high-difficulty questions focusing on ambiguous True/False/Not Given reading distractors and advanced grammatical structures.",
                targetBandLevel = "Band 7.5 - 9.0"
            )
        )
    }

    fun getInitialAchievements(): List<AchievementEntity> {
        return listOf(
            AchievementEntity(
                code = "first_step",
                title = "First Step to Band 9",
                description = "Completed your inaugural English lesson on IELTS Master",
                iconName = "star",
                isUnlocked = true,
                progress = 1,
                maxProgress = 1
            ),
            AchievementEntity(
                code = "streak_7",
                title = "7-Day Study Streak",
                description = "Studied consistently for seven consecutive days",
                iconName = "bolt",
                isUnlocked = false,
                progress = 3,
                maxProgress = 7
            ),
            AchievementEntity(
                code = "vocab_master",
                title = "Lexical Master",
                description = "Learned and reviewed 50 academic vocabulary terms",
                iconName = "menu_book",
                isUnlocked = false,
                progress = 10,
                maxProgress = 50
            ),
            AchievementEntity(
                code = "grammar_guru",
                title = "Grammar Perfectionist",
                description = "Mastered Conditionals, Passive Voice, and Tenses",
                iconName = "spellcheck",
                isUnlocked = false,
                progress = 1,
                maxProgress = 5
            ),
            AchievementEntity(
                code = "speaking_starter",
                title = "Confident Orator",
                description = "Completed Part 2 speaking practice with voice recording",
                iconName = "mic",
                isUnlocked = false,
                progress = 0,
                maxProgress = 1
            ),
            AchievementEntity(
                code = "band7_challenge",
                title = "Band 7.5 Candidate",
                description = "Scored over 75% on an authentic IELTS mock examination",
                iconName = "workspace_premium",
                isUnlocked = false,
                progress = 0,
                maxProgress = 1
            )
        )
    }

    fun getInitialDailyActivities(): List<DailyActivityEntity> {
        return listOf(
            DailyActivityEntity(date = "2026-09-12", minutesStudied = 25, lessonsCompleted = 2, wordsLearned = 5, xpEarned = 70),
            DailyActivityEntity(date = "2026-09-13", minutesStudied = 35, lessonsCompleted = 3, wordsLearned = 8, xpEarned = 95),
            DailyActivityEntity(date = "2026-09-14", minutesStudied = 20, lessonsCompleted = 1, wordsLearned = 4, xpEarned = 75)
        )
    }
}
