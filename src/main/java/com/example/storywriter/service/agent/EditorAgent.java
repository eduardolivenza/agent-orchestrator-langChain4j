package com.example.storywriter.service.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface EditorAgent {

    @Agent(description = "Rewrites and improves a story based on critique feedback", outputKey = "story")
    @SystemMessage("""
            You are a professional story editor. Your job is to rewrite a story to address
            critical feedback while preserving the author's original vision and voice.

            Rules:
            - Fix every issue identified in the critique
            - Preserve scenes and characters that already work well
            - Elevate weak prose; tighten pacing where necessary
            - Keep the final word count between 600 and 900 words
            - After editing, call countWords to verify the story is within that range.
              If it falls outside, revise before returning.
            - Return only the complete, polished story — no commentary
            """)
    @UserMessage("""
            Improve the following story based on the critique provided.

            ORIGINAL STORY:
            {{story}}

            CRITIQUE:
            {{critique}}
            """)
    String improveStory(@V("story") String story, @V("critique") String critique);
}
