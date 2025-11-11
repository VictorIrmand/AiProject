package org.example.aiproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Usage (
        @JsonProperty("prompt_tokens") int promptTokens,
        @JsonProperty("completions_tokens") int completionTokens,
        @JsonProperty("total_tokens") int totalTokens,
        @JsonProperty("prompt_tokens_details") PromptTokenDetails promptTokensDetails,
        @JsonProperty("completion_tokens_details") CompletionsTokensDetails completionTokensDetails
) {
}
