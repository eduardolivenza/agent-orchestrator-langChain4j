package com.example.storywriter.service;

import com.example.storywriter.controller.model.StoryRequest;
import com.example.storywriter.controller.model.StoryResult;
import dev.langchain4j.agentic.UntypedAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StoryOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(StoryOrchestrationService.class);

    private final UntypedAgent storyPipeline;

    public StoryOrchestrationService(UntypedAgent storyPipeline) {
        this.storyPipeline = storyPipeline;
    }

    public StoryResult createStory(StoryRequest request) {
        String topic = buildTopic(request);
        log.info("Orchestrating story creation — topic: \"{}\"", topic);

        long start = System.currentTimeMillis();
        String story = (String) storyPipeline.invoke(Map.of("topic", topic));
        long elapsed = System.currentTimeMillis() - start;
        log.info("Story result: {}", story);
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
