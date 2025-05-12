package com.redcare.github.popularity.services;

import com.redcare.github.popularity.client.GithubClient;
import com.redcare.github.popularity.domain.PopularityScorer;
import com.redcare.github.popularity.model.GithubRepository;
import com.redcare.github.popularity.model.GithubSearchParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @Mock
    private GithubClient githubClient;
    @Mock
    private PopularityScorer popularityScorer;
    @Mock
    private GithubRepository githubRepository;
    @Mock
    private GithubSearchParams searchParams;

    @InjectMocks
    private GithubRepositoryService service;

    @Test
    void shouldReturnEmptyList() {
        when(githubClient.getRepositories(any())).thenReturn(Collections.emptyList());
        var result = service.getRepositoriesWithPopularityScore(searchParams);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCalculateCorrectly() {
        // Arrange
        when(githubRepository.pushedAt()).thenReturn(Instant.now().minus(5, ChronoUnit.DAYS));
        when(githubRepository.starsCount()).thenReturn(100);
        when(githubRepository.forksCount()).thenReturn(50);
        when(githubClient.getRepositories(any())).thenReturn(List.of(githubRepository));
        when(popularityScorer.calculateScore(anyInt(), anyInt(), eq(5))).thenReturn(1.0);
        // Act
        var result = service.getRepositoriesWithPopularityScore(searchParams);
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).popularityScore()).isEqualTo(1.0);
        verify(popularityScorer).calculateScore(eq(100), eq(50), eq(5));
    }
}