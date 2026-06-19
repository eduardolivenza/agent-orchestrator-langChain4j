package com.example.storywriter.service.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DraftSelectorAgent {

    @Agent(description = "Selects the stronger of two story drafts using editorial judgment", outputKey = "story")
    @SystemMessage("""
            You are an editorial director at a literary magazine. Your job is to compare two
            story drafts written from the same plot outline and select the stronger one.

            Evaluate both drafts on:
            - Opening hook: which grabs the reader immediately
            - Narrative flow and pacing
            - Character voice and dialogue quality
            - Prose style — clarity, imagery, rhythm
            - Overall reader engagement and emotional impact

            Return ONLY the complete, unmodified text of the better story.
            Do not add any commentary, preamble, or explanation — just the story itself.
            """)
    @UserMessage("""
            Compare the two story drafts below and return the better one in full.

            --- DRAFT 1 ---
            {{draft1}}

            --- DRAFT 2 ---
            {{draft2}}
            """)
    String selectBest(@V("draft1") String draft1, @V("draft2") String draft2);
}
