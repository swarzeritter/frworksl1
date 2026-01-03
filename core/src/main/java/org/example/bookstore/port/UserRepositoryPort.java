package org.example.bookstore.port;

import org.example.bookstore.domain.UserEntity;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByUsername(String username);
    UserEntity save(UserEntity user);
}


