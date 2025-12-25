package org.example.guestbook;

import java.time.LocalDateTime;

public class Comment {
    private long id;
    private String author;
    private String text;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(long id, String author, String text, LocalDateTime createdAt) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

