package ru.itis.javalab.jlmq.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.javalab.jlmq.server.models.Queue;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByName(String name);
}
