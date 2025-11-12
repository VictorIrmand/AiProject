package org.example.aiproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.dto.ChatRequest;
import org.example.aiproject.dto.ChatResponse;
import org.example.aiproject.dto.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient openAiWebClient;
    private final ChatMemoryService memory;


    public Mono<ChatResponse> ask(Prompt prompt) {
        if (prompt.year() != 0) {
            memory.add("system", "Året er " + prompt.year() + "snak i nutid");
        }
        memory.add("user", prompt.prompt());

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini",
                memory.getAll()
        );

        return openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .doOnNext(res -> {
                    String reply = res.choices().get(0).message().content();
                    memory.add("assistant", reply);
                });
    }


}


