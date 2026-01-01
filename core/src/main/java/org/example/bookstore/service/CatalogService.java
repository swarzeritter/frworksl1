package org.example.bookstore.service;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.port.CatalogRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);
    private final CatalogRepositoryPort catalogRepository;

    public CatalogService(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Page<Book> findBooks(String query, PageRequest pageRequest) {
        if (pageRequest.getPage() < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (pageRequest.getSize() <= 0 || pageRequest.getSize() > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        return catalogRepository.findAll(query, pageRequest);
    }

    public Book findBookById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Book ID must be positive");
        }
        return catalogRepository.findById(id);
    }

    public Book saveBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        // Basic validation
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Author is required");
        }
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        return catalogRepository.save(book);
    }
}
