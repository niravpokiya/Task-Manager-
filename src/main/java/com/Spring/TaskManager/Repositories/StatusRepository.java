package com.Spring.TaskManager.Repositories;

import com.Spring.TaskManager.Entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Status findByName(String name);
}
