package org.example.aiproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.Service.DsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ds")
public class DsController {
    private final static Logger logger = LoggerFactory.getLogger(DsController.class);

    private final DsService service;



    @GetMapping("/cities")
    public Mono<Map<String, String>> loadCities() {
     return service.loadCities();
    }

}
