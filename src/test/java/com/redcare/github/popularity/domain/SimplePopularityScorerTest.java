package com.redcare.github.popularity.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimplePopularityScorerTest {

    @ParameterizedTest(name = "stars={0}, forks={1}, daysSinceUpdate={2} -> score={3}")
    @CsvSource({
            "0, 0, 0, 0.0",
            "10000, 1000, 0, 5.0",   // perfect case, maxed out
            "5000, 300, 50, 2.37",    // middle case
            "10000, 1000, 365, 4.99", // old repo (full penalty)
            "0, 0, 400, 0.0",        // very old, no stars/forks
            "1000, 100, 0, 0.5",     // lower-mid repo
            "10000, 1000, 800, 4.99" // very old, but score is capped correctly
    })
    void shouldCalculateScoreCorrectly(int starCount, int forkCount, int daysSinceUpdate, double expectedScore) {
        var scorer = new SimplePopularityScorer();
        var result = scorer.calculateScore(starCount, forkCount, daysSinceUpdate);
        assertEquals(expectedScore, result);
    }

    @Test
    void shouldAlwaysReturnScoreWithinBounds() {
        var scorer = new SimplePopularityScorer();
        for (int i = 0; i < 1000; i++) {
            var stars = (int) (Math.random() * 50000);
            var forks = (int) (Math.random() * 5000);
            var daysSinceUpdate = (int) (Math.random() * 1000);
            var score = scorer.calculateScore(stars, forks, daysSinceUpdate);
            assertTrue(score >= 0.0 && score <= 5.0,
                    () -> "Score out of bounds for stars=" + stars + ", forks=" + forks + ", days=" + daysSinceUpdate);
        }
    }
}
