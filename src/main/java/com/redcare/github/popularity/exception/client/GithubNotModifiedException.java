package com.redcare.github.popularity.exception.client;

import org.springframework.http.HttpStatus;

public class GithubNotModifiedException extends GithubApiException {
    public GithubNotModifiedException(String message) {
        super(HttpStatus.NOT_MODIFIED, message);
    }
}
