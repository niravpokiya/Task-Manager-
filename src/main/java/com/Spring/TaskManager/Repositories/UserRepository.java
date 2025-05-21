package com.Spring.TaskManager.Repositories;

import com.Spring.TaskManager.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
}
