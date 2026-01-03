package org.example.bookstore.persistence;

import org.example.bookstore.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByBookId(Long bookId);
    List<CommentEntity> findByUserId(Long userId);
}


