package org.example.aiproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.Service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> askAI(@RequestBody String prompt) {

        return openAiService.ask(prompt)
                .map(res -> res.choices().get(0).message().content())
                .map(content -> ResponseEntity.ok().body(content));
    }

}
