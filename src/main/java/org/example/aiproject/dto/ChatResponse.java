package org.example.aiproject.dto;


import java.util.List;

public record ChatResponse (
        String id,
        String object,
        long created,
        String model,
        List<Choice> choices,
        Usage usage
){}
