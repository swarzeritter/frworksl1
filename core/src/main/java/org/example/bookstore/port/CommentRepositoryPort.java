package org.example.bookstore.port;

import org.example.bookstore.domain.CommentEntity;

import java.util.List;

public interface CommentRepositoryPort {
    List<CommentEntity> findByBookId(Long bookId);
    List<CommentEntity> findByUserId(Long userId);
    CommentEntity save(CommentEntity comment);
    CommentEntity findById(Long id);
    void delete(Long id);
}
