package org.example.bookstore.persistence;

import org.example.bookstore.domain.CommentEntity;
import org.example.bookstore.port.CommentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentRepository commentRepository;

    public JpaCommentRepositoryAdapter(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentEntity> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    public List<CommentEntity> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public CommentEntity save(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    @Override
    public CommentEntity findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}


