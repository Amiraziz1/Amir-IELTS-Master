package com.example.ieltsmaster.utils

object BandScoreCalculator {

    fun calculateListeningBand(correctCount: Int): Float {
        return when (correctCount) {
            in 39..40 -> 9.0f
            in 37..38 -> 8.5f
            in 35..36 -> 8.0f
            in 32..34 -> 7.5f
            in 30..31 -> 7.0f
            in 26..29 -> 6.5f
            in 23..25 -> 6.0f
            in 18..22 -> 5.5f
            in 16..17 -> 5.0f
            in 13..15 -> 4.5f
            in 10..12 -> 4.0f
            in 6..9 -> 3.5f
            in 4..5 -> 3.0f
            else -> if (correctCount > 0) 2.5f else 1.0f
        }
    }

    fun calculateReadingAcademicBand(correctCount: Int): Float {
        return when (correctCount) {
            in 39..40 -> 9.0f
            in 37..38 -> 8.5f
            in 35..36 -> 8.0f
            in 33..34 -> 7.5f
            in 30..32 -> 7.0f
            in 27..29 -> 6.5f
            in 23..26 -> 6.0f
            in 19..22 -> 5.5f
            in 15..18 -> 5.0f
            in 13..14 -> 4.5f
            in 10..12 -> 4.0f
            in 6..9 -> 3.5f
            else -> if (correctCount > 0) 2.5f else 1.0f
        }
    }

    fun calculateReadingGeneralBand(correctCount: Int): Float {
        return when (correctCount) {
            40 -> 9.0f
            39 -> 8.5f
            in 37..38 -> 8.0f
            36 -> 7.5f
            in 34..35 -> 7.0f
            in 32..33 -> 6.5f
            in 30..31 -> 6.0f
            in 27..29 -> 5.5f
            in 23..26 -> 5.0f
            in 19..22 -> 4.5f
            in 15..18 -> 4.0f
            in 12..14 -> 3.5f
            else -> if (correctCount > 0) 2.5f else 1.0f
        }
    }

    fun calculateOverallBand(listening: Float, reading: Float, writing: Float, speaking: Float): Float {
        val average = (listening + reading + writing + speaking) / 4.0f
        val decimal = average - average.toInt()
        return when {
            decimal < 0.25f -> average.toInt().toFloat()
            decimal < 0.75f -> average.toInt() + 0.5f
            else -> (average.toInt() + 1).toFloat()
        }
    }

    fun getBandDescriptor(band: Float): String {
        return when {
            band >= 8.5f -> "Expert User (Fluent, accurate, complete understanding)"
            band >= 7.5f -> "Very Good User (Operational command, handles complex argumentation)"
            band >= 6.5f -> "Good User (Effective command despite occasional inaccuracies)"
            band >= 5.5f -> "Competent User (Generally effective in familiar situations)"
            band >= 4.5f -> "Modest User (Partial command, frequent mistakes)"
            else -> "Limited / Intermittent User (Basic survival communication)"
        }
    }
}
