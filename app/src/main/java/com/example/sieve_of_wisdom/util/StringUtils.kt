package com.example.sieve_of_wisdom.util

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

object StringUtils {

    /**
     * Normalizes Vietnamese strings for answer verification.
     * @param removeAccents If true, allows unaccented answers (e.g., "ha noi" matches "Hà Nội").
     */
    fun normalizeAnswer(rawInput: String, removeAccents: Boolean = false): String {
        if (rawInput.isBlank()) return ""

        var text = rawInput.trim()

        if (removeAccents) {
            // Replace Vietnamese 'đ' / 'Đ' manually (Normalizer ignores 'đ')
            text = text.replace('Đ', 'D').replace('đ', 'd')

            // Decompose characters into base letters + combining accent marks
            text = Normalizer.normalize(text, Normalizer.Form.NFD)

            // Strip combining diacritical marks (tone accents)
            val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            text = pattern.matcher(text).replaceAll("")
        } else {
            // Standardize into Canonical Composition (NFC) so NFD keyboard inputs match NFC database entries
            text = Normalizer.normalize(text, Normalizer.Form.NFC)
        }

        return text
            .lowercase(Locale("vi", "VN"))            // Vietnamese lowercase conversion
            .replace(Regex("[^\\p{L}\\p{N}\\s]"), "") // Remove punctuation
            .replace(Regex("\\s+"), " ")        // Rút gọn khoảng trắng kép
            .trim()                             // Trim outer whitespace
    }
}