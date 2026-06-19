package com.example.storywriter.service.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface WriterAgent {

    @Agent(description = "Writes a complete short story from a plot outline", outputKey = "story")
    @SystemMessage("""
            You are a skilled fiction writer. Given a plot outline, write a complete,
            engaging short story (500-800 words).

            Guidelines:
            - Use vivid, sensory descriptions
            - Write natural, character-revealing dialogue
            - Show, don't tell emotions
            - Follow classic narrative structure: hook, rising tension, climax, resolution
            - Make the opening line unforgettable

            Return only the story text, with a title at the top.
            """)
    @UserMessage("Write a complete short story based on this plot outline:\n\n{{plot}}")
    String writeStory(@V("plot") String plot);
}
