package com.example.storywriter.service.guardrail;

import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlotStructureGuardrail implements OutputGuardrail {

    private static final List<String> REQUIRED_KEYWORDS = List.of(
            "title", "genre", "setting", "character", "conflict"
    );

    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        String plot = request.responseFromLLM().aiMessage().text().toLowerCase();
        List<String> missing = REQUIRED_KEYWORDS.stream()
                .filter(kw -> !plot.contains(kw))
                .toList();

        if (!missing.isEmpty()) {
            return reprompt(
                    "Plot outline is missing required sections: " + String.join(", ", missing) + ".",
                    "Rewrite the plot outline and make sure it explicitly includes all of these sections: " +
                    "Title, Genre, Setting, Main Characters, Central Conflict, and Scene/Chapter outline."
            );
        }
        return success();
    }
}
