package com.redcare.github.popularity.exception;

import com.redcare.github.popularity.exception.client.GithubApiException;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<ErrorResponse> handleGithubApiException(GithubApiException ex) {
        var errorResponse = new ErrorResponse(
                ex.getStatus().value(),
                "Github API Error",
                ex.getMessage(),
                ex.toString()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(BeanInstantiationException.class)
    public ResponseEntity<ErrorResponse> handleBeanInstantiationException(BeanInstantiationException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid parameter values",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}