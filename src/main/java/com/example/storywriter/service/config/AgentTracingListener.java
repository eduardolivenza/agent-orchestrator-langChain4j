package com.example.storywriter.service.config;

import dev.langchain4j.agentic.observability.AgentInvocationError;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentRequest;
import dev.langchain4j.agentic.observability.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentTracingListener implements AgentListener {

    private static final Logger log = LoggerFactory.getLogger(AgentTracingListener.class);

    private final ConcurrentHashMap<String, Long> startTimes = new ConcurrentHashMap<>();

    @Override
    public boolean inheritedBySubagents() {
        return true;
    }

    @Override
    public void beforeAgentInvocation(AgentRequest request) {
        startTimes.put(request.agentId(), System.currentTimeMillis());
        log.info("[Agent] --> {} starting  (inputs: {})", request.agentName(), request.inputs().keySet());
    }

    @Override
    public void afterAgentInvocation(AgentResponse response) {
        long elapsed = System.currentTimeMillis() - startTimes.remove(response.agentId());
        String outputSummary = response.output() instanceof String s
                ? s.length() + " chars"
                : String.valueOf(response.output());
        log.info("[Agent] <-- {} finished in {}ms  (output: {})", response.agentName(), elapsed, outputSummary);

        var chatResponse = response.chatResponse();
        if (chatResponse != null && chatResponse.tokenUsage() != null) {
            var usage = chatResponse.tokenUsage();
            log.info("[Agent] tokens  {} — input={} output={} total={}",
                    response.agentName(),
                    usage.inputTokenCount(),
                    usage.outputTokenCount(),
                    usage.totalTokenCount());
        }
    }

    @Override
    public void onAgentInvocationError(AgentInvocationError error) {
        startTimes.remove(error.agentId());
        log.error("[Agent] ✗ {} failed: {}", error.agentName(), error.error().getMessage(), error.error());
    }
}
