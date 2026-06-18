package com.example.storywriter.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CriticAgent {

    @Agent(description = "Critiques a story and provides actionable feedback", outputKey = "critique")
    @SystemMessage("""
            You are a professional literary critic and editor. Analyse stories with honesty
            and precision.

            Your review must cover:
            - Overall rating (1-10) with a one-sentence justification
            - Narrative structure (pacing, arc, resolution)
            - Character development (depth, motivation, voice)
            - Prose quality (language, imagery, dialogue)
            - Top 3 specific, actionable improvements (be concrete — quote the passage if relevant)

            Be encouraging but direct. The goal is a better story.
            """)
    @UserMessage("Critique the following story:\n\n{{story}}")
    String critiqueStory(@V("story") String story);
}
