package com.redcare.github.popularity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Parameters for GitHub repository search operations
 */
public record GithubSearchParams(
        @Schema(description = "ISO date string (YYYY-MM-DD) to filter repositories created on or after this date")
        @Nullable String earliestCreationDate,
        @Schema(description = "Programming language to filter repositories by. The Github Search API will ignore invalid languages values.")
        @Nullable String language,
        @Schema(description = "Maximum number of pages to fetch (1-30)", defaultValue = "1", minimum = "1", maximum = "30")
        Integer maxPages,
        @Schema(description = "Results per page (1-100)", minimum = "1", maximum = "100", defaultValue = "100")
        Integer pageSize
) {
    /**
     * Creates a validated instance of search parameters
     */
    public GithubSearchParams {
        // Validate date format if provided
        if (earliestCreationDate != null && !earliestCreationDate.isEmpty()) {
            // throwing an beanInstantiationException if the format is invalid
            // will be caught by the validation process and global exception handler
            LocalDate.parse(earliestCreationDate, DateTimeFormatter.ISO_DATE);
        }
        // apply constraints to maxPages: use default 1 if null, otherwise ensure it's within 1-30 range
        if (maxPages == null || maxPages < 1) {
            maxPages = 1;
        } else if (maxPages > 30) {
            maxPages = 30;
        }
        // apply constraints to pageSize: use default 100 if null, otherwise ensure it's within 1-100 range
        if (pageSize == null || pageSize > 100) {
            pageSize = 100;
        } else if (pageSize < 1) {
            pageSize = 1;
        }
    }
}