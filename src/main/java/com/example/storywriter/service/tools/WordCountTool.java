package com.example.storywriter.service.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WordCountTool {

    @Tool("Count the words in the given text. Call this after drafting to verify the story meets " +
          "the required word count. If it falls outside the target range, revise before returning.")
    public int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Arrays.stream(text.split("\\s+"))
                           .filter(w -> !w.isBlank())
                           .count();
    }
}
