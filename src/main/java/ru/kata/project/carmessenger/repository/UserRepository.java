package ru.kata.project.carmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.project.carmessenger.model.User;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // findById(String id) уже есть

    // Если нужно найти по username (то же самое что findById)
    // User findByUsername(String username);

    // Если нужно проверить существование
    boolean existsByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
}