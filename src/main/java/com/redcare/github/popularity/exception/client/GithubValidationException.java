package com.redcare.github.popularity.exception.client;

import org.springframework.http.HttpStatus;

public class GithubValidationException extends GithubApiException  {

    public GithubValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
