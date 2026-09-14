package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ieltsmaster.utils.BandScoreCalculator
import com.example.ieltsmaster.utils.ConversationalPartner
import com.example.ieltsmaster.utils.LocalConversationEngine
import com.example.ieltsmaster.utils.MarkdownUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Amir IELTS Master", appName)
  }

  @Test
  fun `test listening band calculator`() {
    val band35 = BandScoreCalculator.calculateListeningBand(35)
    assertEquals(8.0f, band35, 0.01f)

    val band30 = BandScoreCalculator.calculateListeningBand(30)
    assertEquals(7.0f, band30, 0.01f)

    val band23 = BandScoreCalculator.calculateListeningBand(23)
    assertEquals(6.0f, band23, 0.01f)
  }

  @Test
  fun `test overall band rounding`() {
    // 7.0 + 7.5 + 7.0 + 7.0 = 28.5 / 4 = 7.125 -> rounds to 7.0
    val overall1 = BandScoreCalculator.calculateOverallBand(7.0f, 7.5f, 7.0f, 7.0f)
    assertEquals(7.0f, overall1, 0.01f)

    // 7.5 + 7.5 + 7.0 + 7.0 = 29.0 / 4 = 7.25 -> rounds up to 7.5
    val overall2 = BandScoreCalculator.calculateOverallBand(7.5f, 7.5f, 7.0f, 7.0f)
    assertEquals(7.5f, overall2, 0.01f)

    // 7.5 + 8.0 + 7.5 + 8.0 = 31.0 / 4 = 7.75 -> rounds up to 8.0
    val overall3 = BandScoreCalculator.calculateOverallBand(7.5f, 8.0f, 7.5f, 8.0f)
    assertEquals(8.0f, overall3, 0.01f)
  }

  @Test
  fun `test markdown utils strips markers cleanly`() {
    val raw = "This is **very important** and *essential* for __high scores__."
    val cleaned = MarkdownUtils.stripMarkdown(raw)
    assertEquals("This is very important and essential for high scores.", cleaned)
    assertFalse(cleaned.contains("**"))
    assertFalse(cleaned.contains("__"))
    assertFalse(cleaned.contains("*"))
  }

  @Test
  fun `test local conversation engine responds offline`() {
    val result = LocalConversationEngine.processMessage(
      userInput = "Hello, I want to practice my English today.",
      mode = LocalConversationEngine.Mode.DAILY,
      userLevel = "Intermediate",
      partner = ConversationalPartner.EMMA
    )

    assertTrue(result.replyText.isNotBlank())
    assertFalse(result.replyText.contains("**"))
    assertTrue(result.feedback != null)
    assertTrue(result.suggestedReplies.isNotEmpty())
  }

  @Test
  fun `test conversational partner distinct traits and greetings`() {
    val emmaGreeting = LocalConversationEngine.getInitialGreeting(
      mode = LocalConversationEngine.Mode.DAILY,
      userName = "Amir",
      targetBand = 7.5f,
      partner = ConversationalPartner.EMMA
    )
    val davidGreeting = LocalConversationEngine.getInitialGreeting(
      mode = LocalConversationEngine.Mode.DAILY,
      userName = "Amir",
      targetBand = 7.5f,
      partner = ConversationalPartner.DAVID
    )

    assertTrue(emmaGreeting.contains("Emma"))
    assertTrue(davidGreeting.contains("David"))
    assertFalse(emmaGreeting == davidGreeting)
  }

  @Test
  fun `test ielts speaking part 2 prompt cue card`() {
    val result = LocalConversationEngine.processMessage(
      userInput = "I would like to describe a memorable trip I had last summer to the mountains.",
      mode = LocalConversationEngine.Mode.IELTS_PART_2,
      userLevel = "Advanced",
      partner = ConversationalPartner.DAVID
    )

    assertTrue(result.replyText.isNotBlank())
    assertTrue(result.feedback?.fluencyObservation?.isNotBlank() == true || result.feedback?.vocabularyTip?.isNotBlank() == true)
  }
}
