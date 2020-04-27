package ru.itis.javalab.jlmq.server.services.interfaces;

import org.springframework.web.socket.WebSocketSession;
import ru.itis.javalab.jlmq.server.models.Queue;

import java.util.Optional;

public interface JlmqSubscriptionService {

    void subscribe(WebSocketSession session, String queue);

    void unsubscribe(WebSocketSession session);

    Optional<WebSocketSession> subscriber(Queue queue);
}
