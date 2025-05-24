package com.ai.research_assisant.entity;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String content;

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    // Getters and Setters
}
