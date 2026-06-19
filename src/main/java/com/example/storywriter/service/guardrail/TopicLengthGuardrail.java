package com.example.storywriter.service.guardrail;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import org.springframework.stereotype.Component;

@Component
public class TopicLengthGuardrail implements InputGuardrail {

    private static final int MIN = 5;
    private static final int MAX = 500;

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String topic = extractTopic(request.userMessage().singleText());
        if (topic.isBlank() || topic.length() < MIN) {
            return fatal("Topic is too short (minimum " + MIN + " characters).");
        }
        if (topic.length() > MAX) {
            return fatal("Topic is too long (maximum " + MAX + " characters).");
        }
        return success();
    }

    // The user message is the expanded template "…topic: <topic>"; extract after the last colon.
    private String extractTopic(String userMessage) {
        int idx = userMessage.lastIndexOf(':');
        return idx >= 0 ? userMessage.substring(idx + 1).trim() : userMessage.trim();
    }
}
