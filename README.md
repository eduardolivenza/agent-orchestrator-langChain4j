# Agent Orchestrator — LangChain4j

A multi-agent short-story generator built with **Spring Boot** and **LangChain4j Agentic**. It demonstrates non-sequential orchestration: parallel draft generation, agent-driven selection, and a critic-feedback loop that sends stories back for revision until they meet a quality threshold.

## Architecture

### Agents

| Agent | Role | Tools |
|---|---|---|
| `PlotAgent` | Creates a structured plot outline from a topic | `GenreConventionsTool` |
| `WriterAgent` | Writes a 500–800 word story from a plot | `WordCountTool` |
| `DraftSelectorAgent` | Picks the stronger of two drafts using editorial judgment | — |
| `CriticAgent` | Scores and critiques a story (1–10) | `ReadabilityScoreTool` |
| `EditorAgent` | Rewrites a story to address the critique | `WordCountTool` |

### Orchestration flow

```
[1] PlotAgent(topic)
        │
        ▼
[2] WriterAgent(plot) ──┐   ← two drafts written in parallel
    WriterAgent(plot) ──┘
        │
        ▼
[3] DraftSelectorAgent(draft1, draft2)   ← editorial agent picks the better one
        │
        ▼
[4] CriticAgent(story)   ← initial score
        │
        ├─ score ≥ 7  ──────────────────────────────────────────┐
        │                                                        │
        └─ score < 7  →  EditorAgent(story, critique)           │
                              │                                  │
                              └─ CriticAgent(story)  ──────┐    │
                                   (up to 3 revisions)     │    │
                                   score ≥ 7 or max hit ───┘    │
                                                                 │
[5] EditorAgent(story, critique)   ← final polish (always) ◄────┘
```

The two `WriterAgent` calls run on virtual threads via `CompletableFuture`. All agent invocations are traced via `AgentTracingListener` (timing, token usage) registered on each individual agent builder.

### Guardrails

- **`TopicLengthGuardrail`** (input, `PlotAgent`): rejects topics shorter than 5 or longer than 500 characters.
- **`PlotStructureGuardrail`** (output, `PlotAgent`): verifies the outline contains all required sections; reprompts if any are missing.

## Configuration

All LLM settings live in `src/main/resources/application.yml`:

```yaml
app:
  openai:
    api-key: ${OPENAI_API_KEY}
    endpoint: https://api.openai.com/v1
    model-name: gpt-4o-mini
    max-tokens: 2048
```

The project targets any OpenAI-compatible endpoint — set `endpoint` to point at a local proxy or alternative provider.

## Running

```bash
export OPENAI_API_KEY=sk-...
./gradlew bootRun
```

The API is available at `http://localhost:8080`. Interactive docs (Swagger UI) at `http://localhost:8080/swagger-ui.html`.

### Generate a story

```bash
curl -s -X POST http://localhost:8080/api/stories \
  -H "Content-Type: application/json" \
  -d '{"topic": "A lighthouse keeper who discovers the fog is alive", "genre": "horror"}' \
  | jq .
```

Response:

```json
{
  "topic": "A lighthouse keeper who discovers the fog is alive",
  "story": "...",
  "processingTimeMs": 74321
}
```

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Agent framework | LangChain4j Agentic 1.16.3-beta26 |
| Build | Gradle (Kotlin DSL) |
| API docs | SpringDoc OpenAPI 3 |

