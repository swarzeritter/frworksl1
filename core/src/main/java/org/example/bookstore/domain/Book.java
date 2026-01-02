package org.example.bookstore.domain;

import java.time.LocalDateTime;

public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private Integer year;
    private LocalDateTime addedAt;

    public Book() {
        this.addedAt = LocalDateTime.now();
    }

    public Book(Long id, String title, String author, String isbn, String description, Integer year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.year = year;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}

