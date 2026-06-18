package com.example.storywriter.config;

import com.example.storywriter.agent.CriticAgent;
import com.example.storywriter.agent.EditorAgent;
import com.example.storywriter.agent.OrchestratorAgent;
import com.example.storywriter.agent.PlotAgent;
import com.example.storywriter.agent.WriterAgent;
import com.example.storywriter.tools.StoryAgentTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${app.openai.api-key}") String apiKey,
            @Value("${app.openai.endpoint}") String endpoint,
            @Value("${app.openai.model-name}") String modelName,
            @Value("${app.openai.max-tokens}") int maxTokens) {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(endpoint)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .build();
    }

    // ------------------------------------------------------------------ sub-agents
    // Each worker agent is a stateless AiService — no shared memory needed.

    @Bean
    public PlotAgent plotAgent(ChatModel model) {
        return AiServices.builder(PlotAgent.class)
                .chatModel(model)
                .build();
    }

    @Bean
    public WriterAgent writerAgent(ChatModel model) {
        return AiServices.builder(WriterAgent.class)
                .chatModel(model)
                .build();
    }

    @Bean
    public CriticAgent criticAgent(ChatModel model) {
        return AiServices.builder(CriticAgent.class)
                .chatModel(model)
                .build();
    }

    @Bean
    public EditorAgent editorAgent(ChatModel model) {
        return AiServices.builder(EditorAgent.class)
                .chatModel(model)
                .build();
    }

    // ------------------------------------------------------------------ tools

    @Bean
    public StoryAgentTools storyAgentTools(PlotAgent plotAgent, WriterAgent writerAgent,
                                           CriticAgent criticAgent, EditorAgent editorAgent) {
        return new StoryAgentTools(plotAgent, writerAgent, criticAgent, editorAgent);
    }

    // ------------------------------------------------------------------ orchestrator
    // The orchestrator gets a chat memory so it can track which tools it already
    // called within a single story-creation session.

    @Bean
    public OrchestratorAgent orchestratorAgent(ChatModel model,
                                               StoryAgentTools storyAgentTools) {
        return AiServices.builder(OrchestratorAgent.class)
                .chatModel(model)
                .tools(storyAgentTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(30))
                .build();
    }
}
