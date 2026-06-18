package com.example.storywriter.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generated story output")
public record StoryResult(
        @Schema(description = "The original topic from the request")
        String topic,
        @Schema(description = "The final, polished story produced by the agent pipeline")
        String story,
        @Schema(description = "Wall-clock time for the full orchestration run, in milliseconds")
        long processingTimeMs) {
}
