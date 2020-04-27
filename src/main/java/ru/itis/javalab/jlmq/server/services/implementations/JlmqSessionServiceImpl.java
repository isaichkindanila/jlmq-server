package ru.itis.javalab.jlmq.server.services.implementations;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.itis.javalab.jlmq.server.models.Queue;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSessionService;

@Service
public class JlmqSessionServiceImpl implements JlmqSessionService {

    private static final String QUEUE = "_queue";
    private static final String IDLE = "_idle";

    @Override
    public void setQueue(WebSocketSession session, Queue queue) {
        session.getAttributes().put(QUEUE, queue);
    }

    @Override
    public Queue getQueue(WebSocketSession session) {
        Object queue = session.getAttributes().get(QUEUE);
        if (queue == null) {
            throw new IllegalStateException("queue is null");
        }

        return (Queue) queue;
    }

    @Override
    public boolean hasQueue(WebSocketSession session) {
        return session.getAttributes().get(QUEUE) != null;
    }

    @Override
    public void setIdle(WebSocketSession session) {
        session.getAttributes().put(IDLE, Boolean.TRUE);
    }

    @Override
    public void setBusy(WebSocketSession session) {
        session.getAttributes().put(IDLE, Boolean.FALSE);
    }

    @Override
    public boolean isIdle(WebSocketSession session) {
        return session.getAttributes().get(IDLE) == Boolean.TRUE;
    }
}
