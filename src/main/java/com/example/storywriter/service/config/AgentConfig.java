package com.example.storywriter.service.config;

import com.example.storywriter.service.agent.CriticAgent;
import com.example.storywriter.service.agent.EditorAgent;
import com.example.storywriter.service.agent.PlotAgent;
import com.example.storywriter.service.agent.WriterAgent;
import com.example.storywriter.service.guardrail.PlotStructureGuardrail;
import com.example.storywriter.service.guardrail.TopicLengthGuardrail;
import com.example.storywriter.service.tools.GenreConventionsTool;
import com.example.storywriter.service.tools.ReadabilityScoreTool;
import com.example.storywriter.service.tools.WordCountTool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
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
                               GenreConventionsTool genreConventionsTool) {
        return AgenticServices.agentBuilder(PlotAgent.class)
                .chatModel(model)
                .inputGuardrails(topicLengthGuardrail)
                .outputGuardrails(plotStructureGuardrail)
                .tools(genreConventionsTool)
                .build();
    }

    @Bean
    public WriterAgent writerAgent(ChatModel model, WordCountTool wordCountTool) {
        return AgenticServices.agentBuilder(WriterAgent.class)
                .chatModel(model)
                .tools(wordCountTool)
                .build();
    }

    @Bean
    public CriticAgent criticAgent(ChatModel model, ReadabilityScoreTool readabilityScoreTool) {
        return AgenticServices.agentBuilder(CriticAgent.class)
                .chatModel(model)
                .tools(readabilityScoreTool)
                .build();
    }

    @Bean
    public EditorAgent editorAgent(ChatModel model, WordCountTool wordCountTool) {
        return AgenticServices.agentBuilder(EditorAgent.class)
                .chatModel(model)
                .tools(wordCountTool)
                .build();
    }

    // ------------------------------------------------------------------ pipeline
    // Deterministic sequence: Plot → Write → Critique → Edit
    // Each agent reads its inputs from AgenticScope by @V key and writes its
    // output back under the outputKey declared in its @Agent annotation.

    @Bean
    public UntypedAgent storyPipeline(PlotAgent plotAgent, WriterAgent writerAgent,
                                      CriticAgent criticAgent, EditorAgent editorAgent,
                                      AgentTracingListener tracingListener) {
        return AgenticServices.sequenceBuilder()
                .subAgents(plotAgent, writerAgent, criticAgent, editorAgent)
                .outputKey("story")
                .listener(tracingListener)
                .build();
    }
}
