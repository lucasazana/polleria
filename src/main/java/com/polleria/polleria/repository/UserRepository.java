package com.polleria.polleria.repository;

import com.polleria.polleria.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    java.util.List<User> findByRole(String role);
}
