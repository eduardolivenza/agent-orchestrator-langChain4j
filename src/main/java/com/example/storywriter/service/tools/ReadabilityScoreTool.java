package com.example.storywriter.service.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ReadabilityScoreTool {

    @Tool("Calculate objective readability metrics for the given text: word count, sentence count, " +
          "average sentence length, and Flesch Reading Ease score. Call this before writing the critique " +
          "and include the results in the Prose quality section.")
    public String analyseReadability(String text) {
        if (text == null || text.isBlank()) return "Empty text — no metrics available.";

        String[] sentences = Arrays.stream(text.split("[.!?]+"))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);
        String[] words = Arrays.stream(text.split("\\s+"))
                .filter(w -> !w.isBlank())
                .toArray(String[]::new);

        long syllables = Arrays.stream(words).mapToLong(this::countSyllables).sum();

        double asl = (double) words.length / Math.max(sentences.length, 1);
        double asw = (double) syllables / Math.max(words.length, 1);
        double fre = 206.835 - (1.015 * asl) - (84.6 * asw);
        fre = Math.max(0, Math.min(100, fre));

        return ("Words: %d | Sentences: %d | Avg sentence length: %.1f words | " +
                "Flesch Reading Ease: %.1f/100 (%s)")
                .formatted(words.length, sentences.length, asl, fre, interpretFre(fre));
    }

    // Counts vowel groups as a syllable approximation; minimum 1 per word.
    private long countSyllables(String word) {
        long count = word.toLowerCase()
                         .replaceAll("[^aeiou]", " ")
                         .replaceAll("[aeiou]+", "X")
                         .chars()
                         .filter(c -> c == 'X')
                         .count();
        return Math.max(count, 1);
    }

    private String interpretFre(double score) {
        if (score >= 90) return "very easy";
        if (score >= 70) return "easy";
        if (score >= 60) return "standard";
        if (score >= 50) return "fairly difficult";
        if (score >= 30) return "difficult";
        return "very difficult";
    }
}
