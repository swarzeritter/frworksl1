package org.example.bookstore.port;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;

public interface CatalogRepositoryPort {
    Page<Book> findAll(String query, PageRequest pageRequest);
    Book findById(Long id);
    Book save(Book book);
}

