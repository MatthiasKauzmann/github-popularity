package com.redcare.github.popularity.controller;

import com.redcare.github.popularity.model.GithubRepositoryDto;
import com.redcare.github.popularity.model.Language;
import com.redcare.github.popularity.services.GithubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private GithubRepositoryDto repoDto;

    @BeforeEach
    void setUp() {
        when(service.getRepositoriesWithPopularityScore(anyString(), any(Language.class))).thenReturn(List.of(repoDto));
    }

    @Test
    void shouldReturnBadRequestForBadLanguage() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories?earliestCreatedAt=2025-05-11&language=blasbls"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForBadDate() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories?earliestCreatedAt=blabal"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkForCaseInsensitiveLanguage() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories?earliestCreatedAt=2025-05-11&language=JaVA"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForNoArgsAtAll() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForCorrectDate() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories?earliestCreatedAt=2025-05-11"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForBothArgsCorrect() throws Exception {
        this.mockMvc.perform(get("/api/v1/repositories?earliestCreatedAt=2025-05-11&language=java"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
