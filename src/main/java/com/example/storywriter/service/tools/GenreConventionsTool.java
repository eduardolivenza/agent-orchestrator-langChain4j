package com.example.storywriter.service.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GenreConventionsTool {

    private static final Map<String, String> CONVENTIONS = Map.of(
            "mystery", """
                    - Amateur or professional detective protagonist
                    - A crime (usually murder) as the central event
                    - Red herrings to mislead the reader
                    - Clues are fairly planted and discoverable in hindsight
                    - Resolution reveals a single logical culprit
                    - Setting often isolated or claustrophobic (country house, island, snowbound village)
                    """,
            "romance", """
                    - Two protagonists whose relationship is the central arc
                    - Internal and external obstacles keeping them apart
                    - Emotional vulnerability and growth for both characters
                    - A dark moment where the relationship seems doomed
                    - Guaranteed happily-ever-after or happy-for-now ending
                    - Common tropes: enemies-to-lovers, forced proximity, second chance
                    """,
            "thriller", """
                    - High stakes — protagonist's life or many lives at risk
                    - Fast pacing with short chapters and frequent cliffhangers
                    - Ticking-clock tension throughout
                    - Antagonist is formidable and proactive
                    - Protagonist is reactive early, becomes proactive by the climax
                    - Unexpected twists that reframe earlier events
                    """,
            "fantasy", """
                    - A secondary world with its own rules (magic system, geography, politics)
                    - Hero's journey or quest structure
                    - Magic must have costs or limitations to create tension
                    - World-building details woven into action, not info-dumped
                    - Common archetypes: reluctant hero, wise mentor, dark lord
                    - Subgenres: epic, urban, dark, portal fantasy
                    """,
            "sci-fi", """
                    - A speculative technology or scientific concept as the core premise
                    - Explores social, ethical, or philosophical implications of that premise
                    - Characters must be affected personally by the speculative element
                    - Ground the science in plausible extrapolation for reader buy-in
                    - Subgenres: hard SF, space opera, cyberpunk, dystopian, post-apocalyptic
                    """,
            "horror", """
                    - Build atmosphere of dread gradually before explicit scares
                    - Protagonist is vulnerable and increasingly isolated
                    - The threat represents a deeper fear (death, loss of control, the unknown)
                    - Avoid over-explaining the horror — mystery amplifies fear
                    - Subgenres: psychological, supernatural, body horror, cosmic horror
                    """
    );

    @Tool("Returns genre conventions, common tropes, and structural tips for the given story genre. " +
          "Call this when the topic includes a genre hint to produce a more genre-accurate plot outline.")
    public String getGenreConventions(String genre) {
        String key = genre.toLowerCase().trim();
        return CONVENTIONS.getOrDefault(key,
                "No specific conventions found for '" + genre + "'. " +
                "Apply universal storytelling principles: compelling protagonist, clear stakes, " +
                "escalating conflict, and a satisfying resolution.");
    }
}
