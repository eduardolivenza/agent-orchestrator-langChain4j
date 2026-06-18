package com.example.storywriter.tools;

import com.example.storywriter.agent.CriticAgent;
import com.example.storywriter.agent.EditorAgent;
import com.example.storywriter.agent.PlotAgent;
import com.example.storywriter.agent.WriterAgent;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool implementations available to the OrchestratorAgent.
 *
 * Each method delegates to a specialised sub-agent. LangChain4J serialises
 * the @Tool descriptions into the model's tool schema so the orchestrator
 * knows when and how to call each one.
 */
public class StoryAgentTools {

    private static final Logger log = LoggerFactory.getLogger(StoryAgentTools.class);

    private final PlotAgent plotAgent;
    private final WriterAgent writerAgent;
    private final CriticAgent criticAgent;
    private final EditorAgent editorAgent;

    public StoryAgentTools(PlotAgent plotAgent, WriterAgent writerAgent,
                           CriticAgent criticAgent, EditorAgent editorAgent) {
        this.plotAgent = plotAgent;
        this.writerAgent = writerAgent;
        this.criticAgent = criticAgent;
        this.editorAgent = editorAgent;
    }

    @Tool("Create a detailed story plot outline — including title, genre, setting, characters, " +
          "central conflict, and scene structure — for the given topic or premise.")
    public String createPlot(String topic) {
        log.debug("[PlotAgent] generating plot for: {}", topic);
        String result = plotAgent.createPlot(topic);
        log.debug("[PlotAgent] plot ready ({} chars)", result.length());
        return result;
    }

    @Tool("Write a complete short story (500-800 words) based on the provided plot outline.")
    public String writeStory(String plotOutline) {
        log.debug("[WriterAgent] writing story from plot ({} chars)", plotOutline.length());
        String result = writerAgent.writeStory(plotOutline);
        log.debug("[WriterAgent] story ready ({} chars)", result.length());
        return result;
    }

    @Tool("Analyse and critique the story. Returns an overall rating, strengths, " +
          "and the top 3 specific, actionable improvements.")
    public String critiqueStory(String story) {
        log.debug("[CriticAgent] critiquing story ({} chars)", story.length());
        String result = criticAgent.critiqueStory(story);
        log.debug("[CriticAgent] critique ready ({} chars)", result.length());
        return result;
    }

    @Tool("Rewrite and improve the story by addressing the critique. " +
          "Requires both the original story text and the critique feedback.")
    public String improveStory(String story, String critique) {
        log.debug("[EditorAgent] improving story based on critique");
        String result = editorAgent.improveStory(story, critique);
        log.debug("[EditorAgent] final story ready ({} chars)", result.length());
        return result;
    }
}
