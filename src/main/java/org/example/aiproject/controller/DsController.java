package org.example.aiproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.Service.DsService;
import org.example.aiproject.dto.Ej67RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ds")
public class DsController {
    private final static Logger logger = LoggerFactory.getLogger(DsController.class);

    private final DsService service;



    @GetMapping("/areas")
    public Mono<Map<String, String>> getAreas() {
     return service.loadAreas();
    }


    @PostMapping("/areas/{id}/ej67")
    public Mono<String> fetchE67(@PathVariable String id, @RequestBody Ej67RequestDTO dto) {
        logger.info("Fetching EJ67 for area {} with years {}–{}", id, dto.startYear(), dto.endYear());

        Ej67RequestDTO fullDTO = new Ej67RequestDTO(
                List.of(id),
                dto.startYear(),
                dto.endYear(),
                dto.ejendomsKategori()
        );

        return service.loadEJ67(fullDTO);
    }

}
