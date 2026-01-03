package org.example.bookstore.persistence;

import org.example.bookstore.domain.BookEntity;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.port.CatalogRepositoryPort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final BookRepository bookRepository;

    public JpaCatalogRepositoryAdapter(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Page<BookEntity> findAll(String query, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        Sort sort = Sort.unsorted();
        
        if (pageRequest.getSort() != null) {
            sort = Sort.by(pageRequest.getSort());
        }
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        org.springframework.data.domain.Page<BookEntity> jpaPage;
        if (query != null && !query.isBlank()) {
            jpaPage = bookRepository.search(query, pageable);
        } else {
            jpaPage = bookRepository.findAll(pageable);
        }
        
        return new Page<>(jpaPage.getContent(), jpaPage.getNumber(), jpaPage.getSize(), jpaPage.getTotalElements());
    }

    @Override
    public BookEntity findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public BookEntity save(BookEntity book) {
        return bookRepository.save(book);
    }
}


