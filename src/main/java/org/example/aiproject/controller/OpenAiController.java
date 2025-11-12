package org.example.aiproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.Service.OpenAiService;
import org.example.aiproject.dto.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> chat(@RequestBody Prompt prompt) {

        return openAiService.ask(prompt)
                .map(res -> res.choices().get(0).message().content())
                .map(content -> ResponseEntity.ok().body(content));
    }

}
