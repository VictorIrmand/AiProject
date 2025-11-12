package org.example.aiproject.dto;

import java.util.List;

public record Ej67RequestDTO(
        List<String> cityId,
        String startYear,
        String endYear,
        List<String> ejendomsKategori
){}
