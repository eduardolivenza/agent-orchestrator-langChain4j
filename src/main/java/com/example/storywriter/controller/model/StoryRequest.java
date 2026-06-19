package com.example.storywriter.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Input for story generation")
public record StoryRequest(
        @Schema(description = "The story premise or subject", example = "A lighthouse keeper who discovers messages in bottles", requiredMode = Schema.RequiredMode.REQUIRED)
        String topic,
        @Schema(description = "Optional genre hint", example = "mystery")
        String genre) {
}
