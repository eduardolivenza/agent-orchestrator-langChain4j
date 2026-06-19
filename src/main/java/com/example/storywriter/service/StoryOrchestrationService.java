package com.example.storywriter.service;

import com.example.storywriter.controller.model.StoryRequest;
import com.example.storywriter.controller.model.StoryResult;
import com.example.storywriter.service.agent.CriticAgent;
import com.example.storywriter.service.agent.DraftSelectorAgent;
import com.example.storywriter.service.agent.EditorAgent;
import com.example.storywriter.service.agent.PlotAgent;
import com.example.storywriter.service.agent.WriterAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StoryOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(StoryOrchestrationService.class);

    private static final int APPROVAL_THRESHOLD = 7;
    private static final int MAX_REVISIONS = 3;
    private static final Pattern SCORE_PATTERN =
            Pattern.compile("(?i)overall\\s+rating[:\\s]*([0-9]+)\\s*/\\s*10");
    // Matches "**Title:** Foo", "Title: Foo", "## Title\nFoo", etc.
    private static final Pattern TITLE_PATTERN =
            Pattern.compile("(?im)^[#\\s*]*title[*\\s]*:?\\s*(.+)$");

    private final PlotAgent plotAgent;
    private final WriterAgent writerAgent;
    private final DraftSelectorAgent draftSelectorAgent;
    private final CriticAgent criticAgent;
    private final EditorAgent editorAgent;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public StoryOrchestrationService(PlotAgent plotAgent, WriterAgent writerAgent,
                                     DraftSelectorAgent draftSelectorAgent,
                                     CriticAgent criticAgent, EditorAgent editorAgent) {
        this.plotAgent = plotAgent;
        this.writerAgent = writerAgent;
        this.draftSelectorAgent = draftSelectorAgent;
        this.criticAgent = criticAgent;
        this.editorAgent = editorAgent;
    }

    public StoryResult createStory(StoryRequest request) {
        String topic = buildTopic(request);
        log.info("Starting story orchestration — topic: \"{}\"", topic);
        long start = System.currentTimeMillis();

        // 1. Plot (sequential — all else depends on it)
        log.info("[1/4] Generating plot outline...");
        String plot = plotAgent.createPlot(topic);
        String title = extractTitleFromPlot(plot);
        log.info("Title from plot: \"{}\"", title);

        // 2. Write two drafts in parallel, then let an agent pick the better one
        log.info("[2/4] Writing two drafts in parallel...");
        var f1 = CompletableFuture.supplyAsync(() -> writerAgent.writeStory(title, plot), executor);
        var f2 = CompletableFuture.supplyAsync(() -> writerAgent.writeStory(title, plot), executor);
        String draft1 = f1.join();
        String draft2 = f2.join();

        log.info("[3/4] Selecting best draft via editorial agent...");
        String story = draftSelectorAgent.selectBest(draft1, draft2);

        // 4. Critique-revise loop: loop back to editor when critic score is too low
        log.info("[4/4] Critique-revise loop (threshold {}/10, max {} revisions)...",
                APPROVAL_THRESHOLD, MAX_REVISIONS);
        String critique = criticAgent.critiqueStory(story);
        int score = extractScore(critique);
        log.info("  Initial score: {}/10", score);

        for (int rev = 1; rev <= MAX_REVISIONS && score < APPROVAL_THRESHOLD; rev++) {
            log.info("  Score {}/10 below threshold — revision {}/{} in progress...",
                    score, rev, MAX_REVISIONS);
            story = editorAgent.improveStory(story, critique);
            critique = criticAgent.critiqueStory(story);
            score = extractScore(critique);
            log.info("  After revision {}: score {}/10", rev, score);
        }

        if (score >= APPROVAL_THRESHOLD) {
            log.info("Critic approved (score {}/10). Running final polish.", score);
        } else {
            log.info("Max revisions reached (score {}/10). Running final polish anyway.", score);
        }

        String finalStory = editorAgent.improveStory(story, critique);

        long elapsed = System.currentTimeMillis() - start;
        log.info("Story pipeline completed in {}ms", elapsed);
        return new StoryResult(request.topic(), title, stripLeadingTitle(finalStory, title), elapsed);
    }

    private String extractTitleFromPlot(String plot) {
        Matcher m = TITLE_PATTERN.matcher(plot);
        if (m.find()) {
            String title = m.group(1).trim().replaceAll("[*_#]", "").trim();
            if (!title.isBlank()) return title;
        }
        log.warn("Could not extract title from plot — using 'Untitled'");
        return "Untitled";
    }

    // Removes the title line if the LLM included it anyway despite instructions
    private String stripLeadingTitle(String story, String title) {
        String[] lines = story.strip().split("\n", 2);
        if (lines.length > 0) {
            String firstLine = lines[0].trim().replaceAll("[*_#]", "").trim();
            if (firstLine.equalsIgnoreCase(title)) {
                return lines.length > 1 ? lines[1].strip() : "";
            }
        }
        return story.strip();
    }

    private int extractScore(String critique) {
        if (critique == null) return 0;
        Matcher m = SCORE_PATTERN.matcher(critique);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        log.warn("Could not parse numeric score from critique — defaulting to 5");
        return 5;
    }

    private String buildTopic(StoryRequest request) {
        if (request.genre() != null && !request.genre().isBlank()) {
            return request.topic() + " (genre: " + request.genre().trim() + ")";
        }
        return request.topic();
    }
}
