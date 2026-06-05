package com.bluestaq.notes.model;

import java.time.LocalDateTime;

public class NoteResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public NoteResponse(){}

    public NoteResponse(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public NoteResponse(Note note) {
        this.id = note.getId();
        this.content = note.getContent();
        this.createdAt = note.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
