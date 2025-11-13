package org.example.aiproject.dto;

import org.springframework.lang.Nullable;

import java.util.List;

public record Ej67RequestDTO(
        @Nullable List<String> cityId,
        String startYear,
        String endYear,
        List<String> ejendomsKategori
){}
