package com.redcare.github.popularity.exception;

public record ErrorResponse(int status,
                            String error,
                            String message,
                            String log
) {
}
