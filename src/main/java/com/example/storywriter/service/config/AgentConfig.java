package com.example.storywriter.service.config;

import com.example.storywriter.service.agent.CriticAgent;
import com.example.storywriter.service.agent.DraftSelectorAgent;
import com.example.storywriter.service.agent.EditorAgent;
import com.example.storywriter.service.agent.PlotAgent;
import com.example.storywriter.service.agent.WriterAgent;
import com.example.storywriter.service.guardrail.PlotStructureGuardrail;
import com.example.storywriter.service.guardrail.TopicLengthGuardrail;
import com.example.storywriter.service.tools.GenreConventionsTool;
import com.example.storywriter.service.tools.ReadabilityScoreTool;
import com.example.storywriter.service.tools.WordCountTool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
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

    @Bean
    public PlotAgent plotAgent(ChatModel model,
                               TopicLengthGuardrail topicLengthGuardrail,
                               PlotStructureGuardrail plotStructureGuardrail,
                               GenreConventionsTool genreConventionsTool,
                               AgentTracingListener tracingListener) {
        return AgenticServices.agentBuilder(PlotAgent.class)
                .chatModel(model)
                .inputGuardrails(topicLengthGuardrail)
                .outputGuardrails(plotStructureGuardrail)
                .tools(genreConventionsTool)
                .listener(tracingListener)
                .build();
    }

    @Bean
    public WriterAgent writerAgent(ChatModel model, WordCountTool wordCountTool,
                                   AgentTracingListener tracingListener) {
        return AgenticServices.agentBuilder(WriterAgent.class)
                .chatModel(model)
                .tools(wordCountTool)
                .listener(tracingListener)
                .build();
    }

    @Bean
    public CriticAgent criticAgent(ChatModel model, ReadabilityScoreTool readabilityScoreTool,
                                   AgentTracingListener tracingListener) {
        return AgenticServices.agentBuilder(CriticAgent.class)
                .chatModel(model)
                .tools(readabilityScoreTool)
                .listener(tracingListener)
                .build();
    }

    @Bean
    public EditorAgent editorAgent(ChatModel model, WordCountTool wordCountTool,
                                   AgentTracingListener tracingListener) {
        return AgenticServices.agentBuilder(EditorAgent.class)
                .chatModel(model)
                .tools(wordCountTool)
                .listener(tracingListener)
                .build();
    }

    @Bean
    public DraftSelectorAgent draftSelectorAgent(ChatModel model, AgentTracingListener tracingListener) {
        return AgenticServices.agentBuilder(DraftSelectorAgent.class)
                .chatModel(model)
                .listener(tracingListener)
                .build();
    }

}
