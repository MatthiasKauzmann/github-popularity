package com.redcare.github.popularity.controller;

import com.redcare.github.popularity.model.ScoredGithubRepository;
import com.redcare.github.popularity.services.GithubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubPopularityController.class)
class GithubPopularityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GithubRepositoryService service;
    @Mock
    private ScoredGithubRepository repository;

    @BeforeEach
    void setUp() {
        when(service.getRepositoriesWithPopularityScore(any())).thenReturn(List.of(repository));
    }


    static Stream<Arguments> urlTestCases() {
        return Stream.of(
                // Format: description, url, expected status
                Arguments.of("No parameters", "/api/v1/repositories", 200),
                Arguments.of("Valid date", "/api/v1/repositories?earliestCreationDate=2025-05-11", 200),
                Arguments.of("Invalid date", "/api/v1/repositories?earliestCreationDate=blabal", 400),
                Arguments.of("Valid language", "/api/v1/repositories?language=java", 200),
                Arguments.of("Valid maxPages", "/api/v1/repositories?maxPages=5", 200),
                Arguments.of("Invalid maxPages (too high)", "/api/v1/repositories?maxPages=50", 200), // Still valid as it gets capped
                Arguments.of("Valid pageSize", "/api/v1/repositories?pageSize=50", 200),
                Arguments.of("Invalid pageSize (too high)", "/api/v1/repositories?pageSize=200", 200), // Still valid as it gets capped
                Arguments.of("Multiple valid parameters", "/api/v1/repositories?earliestCreationDate=2025-05-11&language=java&maxPages=5&pageSize=50", 200),
                Arguments.of("Mix of valid and invalid parameters", "/api/v1/repositories?earliestCreationDate=blabal&language=java", 400)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("urlTestCases")
    void shouldHandleRepositoryEndpointRequests(String description, String url, int expectedStatus) throws Exception {
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(expectedStatus));
    }
}
