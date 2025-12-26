package org.example.bookstore.port;

import org.example.bookstore.domain.Comment;

import java.util.List;

public interface CommentRepositoryPort {
    List<Comment> findByBookId(Long bookId);
    Comment save(Comment comment);
    Comment findById(Long id);
    void delete(Long id);
}

