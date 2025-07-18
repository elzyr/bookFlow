package com.bookflow.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String userName);
        Optional<User> findById(Long id);

        Boolean existsByUsername(String userName);
        Boolean existsByEmail(String email);
}
