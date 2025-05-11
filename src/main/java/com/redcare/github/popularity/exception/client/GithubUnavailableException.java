package com.redcare.github.popularity.exception.client;

import org.springframework.http.HttpStatus;

public class GithubUnavailableException extends GithubApiException {
    public GithubUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
