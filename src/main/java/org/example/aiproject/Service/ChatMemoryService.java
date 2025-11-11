package org.example.aiproject.Service;


import org.example.aiproject.dto.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMemoryService {
    private final List<Message> messages = new ArrayList<>(
            List.of(new Message("system", "Du er en hjælpsom assistent."))
    );

    public void add(String role, String content) {
        messages.add(new Message(role,content));
    }

    public List<Message> getAll() {
        return new ArrayList<>(messages);
    }

    public void deleteHistory() {
        this.messages.subList(1, messages.size()).clear();
    }
}
