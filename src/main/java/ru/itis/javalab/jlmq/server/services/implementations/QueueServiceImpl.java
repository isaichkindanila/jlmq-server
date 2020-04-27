package ru.itis.javalab.jlmq.server.services.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.javalab.jlmq.server.dto.CreateQueueDto;
import ru.itis.javalab.jlmq.server.models.Queue;
import ru.itis.javalab.jlmq.server.repositories.QueueRepository;
import ru.itis.javalab.jlmq.server.services.interfaces.QueueService;

@Service
@AllArgsConstructor
public class QueueServiceImpl implements QueueService {

    private static final String NAME_PATTERN = "[a-zA-Z0-9/-]{1,255}";

    private final QueueRepository queueRepository;

    @Override
    public void create(CreateQueueDto dto) {
        if (!dto.getName().matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("name does not match " + NAME_PATTERN);
        }

        Queue queue = Queue.builder()
                .name(dto.getName())
                .build();

        queueRepository.save(queue);
    }
}
