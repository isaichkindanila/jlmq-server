package ru.itis.javalab.jlmq.server.services.interfaces;

import org.springframework.web.socket.WebSocketSession;
import ru.itis.javalab.jlmq.server.models.Queue;

public interface JlmqSessionService {

    void setQueue(WebSocketSession session, Queue queue);

    Queue getQueue(WebSocketSession session);

    boolean hasQueue(WebSocketSession session);

    void setIdle(WebSocketSession session);

    void setBusy(WebSocketSession session);

    boolean isIdle(WebSocketSession session);
}
