package com.example.storywriter.controller;

import com.example.storywriter.model.StoryRequest;
import com.example.storywriter.model.StoryResult;
import com.example.storywriter.service.StoryOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stories", description = "AI-powered story generation via multi-agent orchestration")
@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryOrchestrationService service;

    public StoryController(StoryOrchestrationService service) {
        this.service = service;
    }

    @Operation(
        summary = "Generate a story",
        description = "Runs a multi-agent pipeline (Plot → Writer → Critic → Editor) to produce a polished story.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StoryRequest.class),
                examples = @ExampleObject(
                    name = "Mystery example",
                    value = "{\"topic\": \"A lighthouse keeper who discovers messages in bottles\", \"genre\": \"mystery\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Story generated successfully",
                content = @Content(schema = @Schema(implementation = StoryResult.class)))
        }
    )
    @PostMapping
    public ResponseEntity<StoryResult> createStory(@RequestBody StoryRequest request) {
        return ResponseEntity.ok(service.createStory(request));
    }
}
