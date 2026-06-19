package com.example.storywriter.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generated story output")
public record StoryResult(
        @Schema(description = "The original topic from the request")
        String topic,
        @Schema(description = "The story title, derived from the plot outline")
        String title,
        @Schema(description = "The story body (without the title)")
        String story,
        @Schema(description = "Wall-clock time for the full orchestration run, in milliseconds")
        long processingTimeMs) {
}
