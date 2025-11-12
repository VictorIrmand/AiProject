package org.example.aiproject.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    public Mono<Map<String, String>> loadCities() {
        ObjectMapper mapper = new ObjectMapper();

        return dsWebClient.post()
                .uri("/tableinfo")
                .bodyValue(Map.of("table", "BY1", "format", "JSON"))
                .retrieve()
                .bodyToMono(String.class)// gammel api og klienten opfatter det som almindelig tekst.
                .map(json -> { // vi mapper alt json fra den string vi har fået af Danmarks Statistik.
                    try {
                        // Vi parser json-streng til en map.
                        // objectmapper (mapper) er et objekt fra jackson der oversætter json til java-datatyper.
                        // readValue fortæller jackson at json skal laves om til en map af string og object.
                        Map<String, Object> root = mapper.readValue(json, new TypeReference<>() {}); // typerefernce fortæller jackson hvordan json skal konverteres.


                        // vi befinder os i toppen af json filen og vil tilgå den store json array "variables" i toplevel.
                        List<Map<String, Object>> variables = (List<Map<String, Object>>) root.get("variables");

                        System.out.println("Variable IDs: " +
                                variables.stream()
                                        .map(var -> var.get("id"))
                                        .toList());

                        // vi befinder os inden i den store json array "variables" og vil hente json arrayen der hedder "byer".
                        Map<String, Object> cityVariable = variables.stream() // stream operation for at lave en map
                                .filter(var -> "BYER".equals(var.get("id"))) // vi kontrollere at idét under variables er lig med "BYER"
                                .findFirst() // stream finder første gyldige resultat.
                                .orElseThrow(); // smider ex ved fejl



                        // vi vil nu lave en liste med maps. for hver map er der er et id fra en by og et navn fra en by.
                        // vi får byerne ved at få alle "values" på cityvariable og vi tvinger værdien ind på en liste af maps med id og navn som key value.
                        List<Map<String, String>> values = (List<Map<String, String>>) cityVariable.get("values");

                        return values.stream()
                                .filter(value -> !value.get("text").contains("Kommune"))
                                .filter(value -> !value.get("id").endsWith("99997"))
                                .filter(value -> !value.get("id").endsWith("99999"))
                                .collect(Collectors.toMap(value -> value.get("id"), value -> value.get("text")));
                    } catch (Exception e) {
                        throw new RuntimeException("JSON parsing failed", e);
                    }
                });
    }
}