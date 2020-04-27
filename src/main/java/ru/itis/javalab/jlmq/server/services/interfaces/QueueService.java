package ru.itis.javalab.jlmq.server.services.interfaces;

import ru.itis.javalab.jlmq.server.dto.CreateQueueDto;

public interface QueueService {

    void create(CreateQueueDto dto);
}
