package com.redcare.github.popularity.domain;

import org.springframework.stereotype.Service;

/**
 * A simple popularity scorer that uses weighted scores for stars, forks, and days since
 * update. A higher score indicates a higher popularity.
 * The scores are normalized to the range [0, 5] and rounded to two decimals places.
 * Star and fork counts as well as days since update are capped given GitHub statistics and an opinionated approach.
 */
@Service
public class SimplePopularityScorer implements PopularityScorer {
    // WEIGHTS
    private static final double STAR_WEIGHT = 1.0;
    private static final double FORK_WEIGHT = 1.5;
    private static final double UPDATE_PENALTY_PER_DAY = 0.05;
    // CAPS
    // max 300k, 100k for popular
    private static final int STAR_COUNT_CAP = 10000;
    // max 50k, 15k for popular
    private static final int FORK_COUNT_CAP = 1000;
    // 365 days should be the ceiling so a 2-year duration doesn't get twice the penalty
    private static final int UPDATE_PENALTY_DAYS_CAP = 365;
    // SCORE BOUNDS
    private static final double MAX_RAW_SCORE = STAR_COUNT_CAP * STAR_WEIGHT + FORK_COUNT_CAP * FORK_WEIGHT;
    private static final double MIN_NORMALIZED_SCORE = 0.0;
    private static final double MAX_NORMALIZED_SCORE = 5.0;
    // DECIMALS
    private static final double ROUNDING_SCALE = 100.0;

    /**
     * Calculates a popularity score for a GitHub repository based on stars, forks, and recency of updates.
     * The score is calculated by applying weights to star and fork counts, subtracting a penalty for days
     * since the last update, normalizing the result to a scale of 0-5, and rounding to two decimal places.
     *
     * @param starCount       The number of stars the repository has received
     * @param forkCount       The number of times the repository has been forked
     * @param daysSinceUpdate The number of days since the repository was last updated
     * @return A normalized popularity score between 0.0 and 5.0, with higher values indicating greater popularity
     */
    @Override
    public double calculateScore(int starCount, int forkCount, int daysSinceUpdate) {
        var cappedDays = Math.min(daysSinceUpdate, UPDATE_PENALTY_DAYS_CAP);
        var score = STAR_WEIGHT * starCount + FORK_WEIGHT * forkCount - cappedDays * UPDATE_PENALTY_PER_DAY;
        // bound to min normalized score
        score = Math.max(MIN_NORMALIZED_SCORE, score);
        // normalize between min and max normalized score
        score = (score / MAX_RAW_SCORE) * MAX_NORMALIZED_SCORE;
        // bound max to max normalized score
        score = Math.min(score, MAX_NORMALIZED_SCORE);
        // round to 2 decimal places
        return Math.round(score * ROUNDING_SCALE) / ROUNDING_SCALE;
    }
}
