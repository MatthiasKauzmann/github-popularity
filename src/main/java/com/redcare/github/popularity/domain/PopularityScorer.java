package com.redcare.github.popularity.domain;

public interface PopularityScorer {

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
    double calculateScore(int starCount, int forkCount, int daysSinceUpdate);
}
