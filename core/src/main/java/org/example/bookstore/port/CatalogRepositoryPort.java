package org.example.bookstore.port;

import org.example.bookstore.domain.BookEntity;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;

public interface CatalogRepositoryPort {
    Page<BookEntity> findAll(String query, PageRequest pageRequest);
    BookEntity findById(Long id);
    BookEntity save(BookEntity book);
}
