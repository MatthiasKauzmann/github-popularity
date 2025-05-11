package com.redcare.github.popularity.exception.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class GithubApiException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public GithubApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
