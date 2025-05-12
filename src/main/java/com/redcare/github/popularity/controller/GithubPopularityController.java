package com.redcare.github.popularity.controller;

import com.redcare.github.popularity.model.GithubSearchParams;
import com.redcare.github.popularity.model.ScoredGithubRepository;
import com.redcare.github.popularity.services.GithubRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Tag(name = "GitHub Repository Popularity", description = "API to retrieve GitHub repositories with popularity scores")
public class GithubPopularityController {

    private final GithubRepositoryService repositoryService;

    /**
     * Retrieves GitHub repositories with their calculated popularity scores based on the provided search parameters.
     *
     * @param searchParams The parameters to filter GitHub repositories, including language, earliest creation date,
     *                     and pagination options
     * @return A ResponseEntity containing a list of GitHub repositories with their popularity scores
     */
    @Operation(
            summary = "Get GitHub repositories with popularity scores",
            description = "Retrieves GitHub repositories filtered by the provided parameters and calculates a popularity score for each repository based on stars, forks, and other metrics"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved repositories",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScoredGithubRepository.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "GitHub API rate limit exceeded",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or GitHub API error",
                    content = @Content
            )
    })
    @GetMapping("/repositories")
    public ResponseEntity<List<ScoredGithubRepository>> getRepositoriesWithPopularityScore(GithubSearchParams searchParams) {
        {
            var result = repositoryService.getRepositoriesWithPopularityScore(searchParams);
            return ResponseEntity.ok(result);
        }
    }
}
