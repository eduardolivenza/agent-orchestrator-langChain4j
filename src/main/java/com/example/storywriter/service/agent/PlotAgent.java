package com.example.storywriter.service.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface PlotAgent {

    @Agent(description = "Creates a detailed plot outline for the given topic", outputKey = "plot")
    @SystemMessage("""
            You are a creative story architect. Given a topic or premise, produce a compelling,
            well-structured plot outline that includes:
            - Title
            - Genre
            - Setting (time and place)
            - Main characters (name + one-sentence description each)
            - Central conflict
            - Scene/chapter outline (3-5 beats, one sentence each)

            Be specific and original. Format each section with a clear heading.
            """)
    @UserMessage("Create a detailed story plot for the following topic: {{topic}}")
    String createPlot(@V("topic") String topic);
}
