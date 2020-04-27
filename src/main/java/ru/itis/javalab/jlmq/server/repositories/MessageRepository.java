package ru.itis.javalab.jlmq.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.javalab.jlmq.server.models.Message;
import ru.itis.javalab.jlmq.server.models.Message.Status;
import ru.itis.javalab.jlmq.server.models.Queue;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByToken(String token);

    Optional<Message> findFirstByQueueAndStatusIn(Queue queue, Collection<Status> statuses);

    default Optional<Message> findFirstUnprocessed(Queue queue) {
        return findFirstByQueueAndStatusIn(queue, EnumSet.of(Status.RECEIVED, Status.ACKNOWLEDGED));
    }
}
