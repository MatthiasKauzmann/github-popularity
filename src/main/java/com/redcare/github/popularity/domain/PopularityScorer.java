package com.redcare.github.popularity.domain;

/**
 * Calculates a popularity score for a GitHub repository based on stars, forks, and update recency.
 */
public interface PopularityScorer {

    /**
     * Calculates a popularity score.
     *
     * @param starCount       number of stars the repository has
     * @param forkCount       number of forks the repository has
     * @param daysSinceUpdate number of days since the last repository update
     * @return a popularity score
     */
    double calculateScore(int starCount, int forkCount, int daysSinceUpdate);
}
