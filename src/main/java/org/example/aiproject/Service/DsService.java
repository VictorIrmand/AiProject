package org.example.aiproject.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.aiproject.dto.Ej67RequestDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DsService {
    ObjectMapper mapper = new ObjectMapper();
    private final WebClient dsWebClient;

    public Mono<Map<String, String>> loadAreas() {

        return dsWebClient.get()
                .uri("/tableinfo/BOL101?format=JSON")
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        // Parse JSON til Java Map
                        Map<String, Object> root = mapper.readValue(json, new TypeReference<>() {});

                        // Hent listen af "variables" fra JSON-topniveauet
                        List<Map<String, Object>> variables = (List<Map<String, Object>>) root.get("variables");

                        // Find variablen "OMRÅDE" — den indeholder kommunerne
                        Map<String, Object> areaVariable = variables.stream()
                                .filter(var -> "OMRÅDE".equals(var.get("id")))
                                .findFirst()
                                .orElseThrow();

                        // Hent alle værdier (kommuner/områder) som liste af {id, text}
                        List<Map<String, String>> values = (List<Map<String, String>>) areaVariable.get("values");

                        // Filtrér og konverter til Map<id, text>
                        return values.stream()
                                .filter(v -> !v.get("id").endsWith("99999")) // fjern "Landdistrikter"
                                .filter(v -> !v.get("id").endsWith("99997")) // fjern "Uden fast bopæl"
                                .filter(v -> v.get("text").contains("Landsdel") && !v.get("text").contains("Hele landet") && !v.get("text").contains("Region"))
                                .collect(Collectors.toMap(
                                        v -> v.get("id"),
                                        v -> v.get("text"),
                                        (a, b) -> a // behold første ved dubletter
                                ));

                    } catch (Exception e) {
                        throw new RuntimeException("JSON parsing failed", e);
                    }
                });
    }

    public Mono<String> loadEJ67 (Ej67RequestDTO dto) {
        Map<String, Object> body = Map.of(
                "table", "EJ67",
                "format", "JSONSTAT",
                "filter", List.of(
                        // danmarks statistik kræver en liste af strings, da man skal kunne sende flere værdier.
                        Map.of("variable", "OMRÅDE", "values", dto.cityId()), // hvilken landsdel.
                        Map.of("variable", "EJENDOMSKATE", "values", dto.ejendomsKategori()), // hvilken type af ejendom. "ejerlejlighed" "ensfamiliehuse" f.esk.
                        Map.of("variable", "TAL", "values", List.of("100")), // 100 er indekset for prisen på ejendommen.
                        Map.of("variable", "Tid", "values", List.of(dto.startYear(), dto.endYear())) // hvilket år der skal hentes fra.
                )
        );

        return dsWebClient.post()
                .uri("/data")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new RuntimeException("DS API error: " + msg)))
                )
                .bodyToMono(String.class);
    }
}