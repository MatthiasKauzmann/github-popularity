package com.redcare.github.popularity.exception.client;

import org.springframework.http.HttpStatus;

public class GithubRateLimitException extends GithubApiException {
    public GithubRateLimitException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
