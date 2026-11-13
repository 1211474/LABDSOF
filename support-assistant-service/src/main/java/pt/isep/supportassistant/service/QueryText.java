package pt.isep.supportassistant.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

final class QueryText {

    private QueryText() {
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String withoutDiacritics = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return withoutDiacritics.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    static boolean containsKeyword(String normalizedText, String normalizedKeyword) {
        if (normalizedKeyword == null || normalizedKeyword.isBlank()) {
            return false;
        }

        Pattern keywordPattern = Pattern.compile(
                "(?<![\\p{L}\\p{N}])" + Pattern.quote(normalizedKeyword) + "\\p{L}*",
                Pattern.UNICODE_CHARACTER_CLASS
        );

        return keywordPattern.matcher(normalizedText).find();
    }

    static boolean containsPhrase(String normalizedText, String normalizedPhrase) {
        if (normalizedPhrase == null || normalizedPhrase.isBlank()) {
            return false;
        }

        Pattern phrasePattern = Pattern.compile(
                "(?<![\\p{L}\\p{N}])" + Pattern.quote(normalizedPhrase) + "(?![\\p{L}\\p{N}])",
                Pattern.UNICODE_CHARACTER_CLASS
        );

        return phrasePattern.matcher(normalizedText).find();
    }
}
