package org.example.bookstore.service;

import org.example.bookstore.domain.Comment;
import org.example.bookstore.port.CommentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private static final Duration DELETE_WINDOW = Duration.ofHours(24);
    
    private final CommentRepositoryPort commentRepository;

    public CommentService(CommentRepositoryPort commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findCommentsByBookId(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be positive");
        }
        return commentRepository.findByBookId(bookId);
    }

    public Comment createComment(Long bookId, String author, String text) {
        validateComment(author, text);
        
        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setAuthor(author.trim());
        comment.setText(text.trim());
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        
        LocalDateTime now = LocalDateTime.now();
        Duration age = Duration.between(comment.getCreatedAt(), now);
        
        if (age.compareTo(DELETE_WINDOW) > 0) {
            throw new IllegalStateException("Comment can only be deleted within 24 hours of creation");
        }
        
        commentRepository.delete(commentId);
    }

    private void validateComment(String author, String text) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }
        if (author.trim().length() > 64) {
            throw new IllegalArgumentException("Author must not exceed 64 characters");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text is required");
        }
        if (text.trim().length() > 1000) {
            throw new IllegalArgumentException("Text must not exceed 1000 characters");
        }
    }
}

