package org.example.aiproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.aiproject.dto.ChatRequest;
import org.example.aiproject.dto.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient openAiWebClient;
    private final ChatMemoryService memory;


    public Mono<ChatResponse> ask(String prompt) {
        if (memory.getAll().isEmpty()) {
            memory.add("system",
                    "Du er en hjælpsom og præcis assistent, der forklarer udviklingen i danske boligpriser. " +
                            "Svar altid på dansk, kort og klart. " +
                            "Brug kun de data, du får fra Danmarks Statistik, som kilde. " +
                            "Hvis noget mangler i dataen, skal du sige det i stedet for at gætte. " +
                            "Du vil nu modtage kontekstdata fra Danmarks Statistik — " +
                            "men du må ikke svare endnu. Vent med at svare, indtil du bliver spurgt direkte."
            );
        }
        memory.add("user", prompt);

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


