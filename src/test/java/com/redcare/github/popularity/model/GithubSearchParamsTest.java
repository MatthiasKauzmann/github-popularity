package com.redcare.github.popularity.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GithubSearchParamsTest {
    @Test
    void shouldAcceptValidDate() {
        // Given a valid ISO date
        String validDate = "2023-01-15";
        // When creating search params with this date
        var params = new GithubSearchParams(validDate, "java", 1, 100);
        // Then it should be accepted without exceptions
        assertThat(params.earliestCreationDate()).isEqualTo(validDate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023/01/15", "15-01-2023", "01-15-2023", "2023-13-01", "2023-01-32", "invalid"})
    void shouldRejectInvalidDateFormats(String invalidDate) {
        assertThatThrownBy(() -> new GithubSearchParams(invalidDate, "java", 1, 100))
                .isInstanceOf(DateTimeParseException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1",    // Default value when 0
            "1, 1",    // Minimum value
            "15, 15",  // Middle value
            "30, 30",  // Maximum value
            "31, 30",  // Above maximum, should cap to 30
            "-5, 1"    // Below minimum, should cap to 1
    })
    void shouldHandleMaxPagesCorrectly(int input, int expected) {
        var params = new GithubSearchParams("2023-01-01", "java", input, 100);
        assertThat(params.maxPages()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1",   // Default value when 0
            "1, 1",     // Minimum value
            "50, 50",   // Middle value
            "100, 100", // Maximum value
            "101, 100", // Above maximum, should cap to 100
            "-5, 1"     // Below minimum, should cap to 1
    })
    void shouldHandlePageSizeCorrectly(int input, int expected) {
        var params = new GithubSearchParams("2023-01-01", "java", 1, input);
        assertThat(params.pageSize()).isEqualTo(expected);
    }

    @Test
    void shouldApplyDefaultValuesWhenNull() {
        var params = new GithubSearchParams("2023-01-01", "java", null, null);
        assertThat(params.maxPages()).isEqualTo(1);
        assertThat(params.pageSize()).isEqualTo(100);
    }
}
