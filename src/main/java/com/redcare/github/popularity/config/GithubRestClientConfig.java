package com.redcare.github.popularity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GithubRestClientConfig {

    private final String accessToken;

    public GithubRestClientConfig(@Value("${github.access-token:''}") String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Configures a RestClient For GitHub requests.
     * Will issue authenticated requests if an access token is provided at start-up.
     */
    @Bean
    public RestClient restClient() {
        var builder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader("Accept", "application/vnd.github+json");
        if (accessToken != null && !accessToken.isEmpty()) {
            builder.defaultHeader("Authorization", "Bearer " + accessToken);
        }
        return builder.build();
    }
}
