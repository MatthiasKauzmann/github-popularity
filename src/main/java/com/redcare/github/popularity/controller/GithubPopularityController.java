package com.redcare.github.popularity.controller;

import com.redcare.github.popularity.model.Language;
import com.redcare.github.popularity.model.ScoredGithubRepository;
import com.redcare.github.popularity.services.GithubRepositoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class GithubPopularityController {

    private final GithubRepositoryService repositoryService;

    /**
     * Retrieves GitHub repositories with their calculated popularity scores.
     * This endpoint allows filtering repositories by creation date and programming language.
     * If invalid parameters are provided, appropriate exceptions will be thrown.
     *
     * @param earliestCreatedAt Optional ISO date string (YYYY-MM-DD) to filter repositories
     *                          created on or after this date
     * @param language          Optional programming language to filter repositories by. Must match one of the
     *                          values defined in the Language enum (case-insensitive).
     * @return ResponseEntity containing a list of GitHub repositories with their popularity scores
     */
    @GetMapping("/repositories")
    public ResponseEntity<List<ScoredGithubRepository>> getRepositoriesWithPopularityScore(@RequestParam(required = false) String earliestCreatedAt, @Parameter(
            description = "Programming language to filter repositories by",
            schema = @Schema(implementation = Language.class, type = "string",
                    allowableValues = {"JAVA", "JAVASCRIPT", "PYTHON", "TYPESCRIPT", "C", "CPP",
                            "CSHARP", "GO", "RUBY", "PHP", "SWIFT", "KOTLIN", "SCALA", "RUST",
                            "DART", "OBJECTIVE_C", "SHELL", "HTML", "CSS", "VUE", "HASKELL",
                            "ELIXIR", "PERL", "LUA", "GROOVY", "POWER_SHELL", "MATLAB",
                            "VISUAL_BASIC", "FSHARP", "ERLANG", "SQL", "JULIA", "COFFEESCRIPT",
                            "OCAML", "R", "MAKEFILE", "ASSEMBLY", "BATCH", "CMAKE", "FORTRAN",
                            "COMMON_LISP", "PASCAL", "SMALLTALK", "VHDL"})
    ) @RequestParam(required = false) String language) {
        if (earliestCreatedAt != null && !earliestCreatedAt.isEmpty()) {
            // letting parse throw a DateTimeParseException if the input is not a valid date
            LocalDate.parse(earliestCreatedAt, DateTimeFormatter.ISO_DATE);
        }
        Language languageEnum = null;
        if (language != null) {
            // letting valueOf throw an IllegalArgumentException if the input is not a valid language
            languageEnum = Language.valueOf(language.toUpperCase());
        }
        var result = repositoryService.getRepositoriesWithPopularityScore(earliestCreatedAt, languageEnum);
        return ResponseEntity.ok(result);
    }
}
