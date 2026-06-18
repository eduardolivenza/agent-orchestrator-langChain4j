package com.example.storywriter.service;

import com.example.storywriter.agent.OrchestratorAgent;
import com.example.storywriter.model.StoryRequest;
import com.example.storywriter.model.StoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StoryOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(StoryOrchestrationService.class);

    private final OrchestratorAgent orchestratorAgent;

    public StoryOrchestrationService(OrchestratorAgent orchestratorAgent) {
        this.orchestratorAgent = orchestratorAgent;
    }

    public StoryResult createStory(StoryRequest request) {
        String topic = buildTopic(request);
        log.info("Orchestrating story creation — topic: \"{}\"", topic);

        long start = System.currentTimeMillis();
        String story = orchestratorAgent.createStory(topic);
        long elapsed = System.currentTimeMillis() - start;

        log.info("Story pipeline completed in {}ms", elapsed);
        return new StoryResult(request.topic(), story, elapsed);
    }

    private String buildTopic(StoryRequest request) {
        if (request.genre() != null && !request.genre().isBlank()) {
            return request.topic() + " (genre: " + request.genre().trim() + ")";
        }
        return request.topic();
    }
}
