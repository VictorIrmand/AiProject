package org.example.aiproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PromptTokenDetails (
        @JsonProperty("cached_tokens") int cachedTokens
) {
}
