package org.example.aiproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompletionsTokensDetails(
        @JsonProperty("reasoning_tokens") int reasoningTokens,

        @JsonProperty("accepted_prediction_tokens") int acceptedPredictionTokens,

        @JsonProperty("rejected_prediction_tokens") int rejectedPredictionTokens
) {
}
