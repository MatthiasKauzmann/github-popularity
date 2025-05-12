package com.redcare.github.popularity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    /**
     * Enables Jackson object mapper to map GitHub's incoming date type.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
