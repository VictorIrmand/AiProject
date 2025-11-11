package org.example.aiproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.Service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> chat(@RequestBody String prompt, @RequestBody int year) {

        return openAiService.ask(prompt, year)
                .map(res -> res.choices().get(0).message().content())
                .map(content -> ResponseEntity.ok().body(content));
    }

}
