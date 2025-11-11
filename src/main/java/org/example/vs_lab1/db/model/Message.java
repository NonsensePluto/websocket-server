package org.example.vs_lab1.db.model;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private String senderIp;

    // Конструкторы
    public Message() {}

    public Message(Long id, String text, LocalDateTime createdAt, String senderIp) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.senderIp = senderIp;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSenderIp() { return senderIp; }
    public void setSenderIp(String senderIp) { this.senderIp = senderIp; }

    @Override
    public String toString() {
        return String.format("Message{id=%d, text='%s', createdAt=%s, senderIp='%s'}",
                id, text, createdAt, senderIp);
    }
}
