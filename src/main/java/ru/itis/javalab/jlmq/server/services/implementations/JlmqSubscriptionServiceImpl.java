package ru.itis.javalab.jlmq.server.services.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.itis.javalab.jlmq.server.models.Queue;
import ru.itis.javalab.jlmq.server.repositories.QueueRepository;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSessionService;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSubscriptionService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JlmqSubscriptionServiceImpl implements JlmqSubscriptionService {

    private final QueueRepository queueRepository;
    private final JlmqSessionService sessionService;

    private final Map<Queue, WebSocketSession> subscriptions = new HashMap<>();

    @Override
    public void subscribe(WebSocketSession session, String queueName) {
        if (sessionService.hasQueue(session)) {
            throw new IllegalStateException("already subscribed");
        }

        Queue queue = queueRepository.findByName(queueName)
                .orElseThrow(IllegalArgumentException::new);

        if (subscriptions.get(queue) != null) {
            throw new IllegalStateException("queue is busy");
        }

        sessionService.setQueue(session, queue);
        subscriptions.put(queue, session);
    }

    @Override
    public void unsubscribe(WebSocketSession session) {
        if (sessionService.hasQueue(session)) {
            subscriptions.remove(sessionService.getQueue(session));
        }
    }

    @Override
    public Optional<WebSocketSession> subscriber(Queue queue) {
        return Optional.ofNullable(subscriptions.get(queue));
    }
}
