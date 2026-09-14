package com.example.ieltsmaster.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object MarkdownUtils {

    /**
     * Completely strips raw Markdown tokens (e.g. **, *, __, _, #, `, >)
     * so that plain text labels and TTS never speak or display raw markdown symbols.
     */
    fun stripMarkdown(input: String?): String {
        if (input.isNullOrBlank()) return ""
        return input
            .replace(Regex("""\*\*(.*?)\*\*"""), "$1")
            .replace(Regex("""__(.*?)__"""), "$1")
            .replace(Regex("""\*(.*?)\*"""), "$1")
            .replace(Regex("""_(.*?)_"""), "$1")
            .replace(Regex("""`{1,3}(.*?)`{1,3}"""), "$1")
            .replace(Regex("""^#{1,6}\s+""", RegexOption.MULTILINE), "")
            .replace(Regex("""^>\s+""", RegexOption.MULTILINE), "")
            .replace(Regex("""~~(.*?)~~"""), "$1")
            .trim()
    }

    /**
     * Converts text containing Markdown into a richly formatted Jetpack Compose AnnotatedString
     * with proper bold and italic styles without any raw delimiter characters.
     */
    fun parseMarkdownToAnnotatedString(
        input: String?,
        primaryColor: Color = Color.Unspecified
    ): AnnotatedString {
        if (input.isNullOrBlank()) return buildAnnotatedString { append("") }

        return buildAnnotatedString {
            val lines = input.lines()
            for ((lineIndex, line) in lines.withIndex()) {
                var workingLine = line

                // Handle header prefixes
                val isHeader1 = workingLine.startsWith("# ")
                val isHeader2 = workingLine.startsWith("## ")
                val isHeader3 = workingLine.startsWith("### ")
                val isBullet = workingLine.trimStart().startsWith("- ") || workingLine.trimStart().startsWith("* ")

                if (isHeader1) workingLine = workingLine.removePrefix("# ")
                if (isHeader2) workingLine = workingLine.removePrefix("## ")
                if (isHeader3) workingLine = workingLine.removePrefix("### ")

                if (isBullet) {
                    val indent = workingLine.takeWhile { it == ' ' }
                    workingLine = indent + "• " + workingLine.trimStart().removePrefix("- ").removePrefix("* ")
                }

                if (isHeader1 || isHeader2 || isHeader3) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = if (primaryColor != Color.Unspecified) primaryColor else Color.Unspecified
                        )
                    ) {
                        appendFormattedInline(workingLine)
                    }
                } else {
                    appendFormattedInline(workingLine)
                }

                if (lineIndex < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendFormattedInline(line: String) {
        // Regex matches **bold**, *italic*, and `code`
        val tokenRegex = Regex("""(\*\*.*?\*\*|\*.*?\*|`.*?`)""")
        var lastIndex = 0

        for (match in tokenRegex.findAll(line)) {
            if (match.range.first > lastIndex) {
                append(line.substring(lastIndex, match.range.first))
            }
            val token = match.value
            when {
                token.startsWith("**") && token.endsWith("**") && token.length >= 4 -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(token.substring(2, token.length - 2))
                    }
                }
                token.startsWith("*") && token.endsWith("*") && token.length >= 2 -> {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(token.substring(1, token.length - 1))
                    }
                }
                token.startsWith("`") && token.endsWith("`") && token.length >= 2 -> {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(token.substring(1, token.length - 1))
                    }
                }
                else -> {
                    append(token)
                }
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < line.length) {
            append(line.substring(lastIndex))
        }
    }
}
