package org.example.bookstore.service;

import org.example.bookstore.domain.BookEntity;
import org.example.bookstore.domain.CommentEntity;
import org.example.bookstore.domain.UserEntity;
import org.example.bookstore.port.CatalogRepositoryPort;
import org.example.bookstore.port.CommentRepositoryPort;
import org.example.bookstore.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private static final Duration DELETE_WINDOW = Duration.ofHours(24);
    
    private final CommentRepositoryPort commentRepository;
    private final CatalogRepositoryPort catalogRepository;
    private final UserRepositoryPort userRepository;

    public CommentService(CommentRepositoryPort commentRepository, CatalogRepositoryPort catalogRepository, UserRepositoryPort userRepository) {
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.userRepository = userRepository;
    }

    public List<CommentEntity> findCommentsByBookId(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be positive");
        }
        return commentRepository.findByBookId(bookId);
    }
    
    public List<CommentEntity> getCommentsByUser(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Transactional
    public CommentEntity createComment(Long bookId, String authorName, String text) {
        validateComment(authorName, text);
        
        BookEntity book = catalogRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        
        // Find or create user
        UserEntity user = userRepository.findByUsername(authorName)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity(authorName, "", "USER");
                    return userRepository.save(newUser);
                });
        
        CommentEntity comment = new CommentEntity();
        comment.setBook(book);
        comment.setUser(user);
        comment.setText(text.trim());
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId);
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
