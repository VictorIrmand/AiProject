package org.example.aiproject.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public WebClient openAiWebClient() {

        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient dsWebClient() {
        return WebClient.builder() // Starter en ny WebClient-builder.
                .baseUrl("https://api.statbank.dk/v1") // Sætter basis-URL, så du kun behøver skrive fx ".uri("/tableinfo")" senere.
                .defaultHeader(HttpHeaders.ACCEPT, "application/json, text/json") // Fortæller serveren at klienten kan modtage både "application/json" og "text/json".
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Fortæller at alt du sender er i JSON-format.
                .build(); // Bygger og returnerer WebClient-objektet.
    }
}
