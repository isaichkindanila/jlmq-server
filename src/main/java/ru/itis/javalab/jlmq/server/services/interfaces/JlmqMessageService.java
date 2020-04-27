package ru.itis.javalab.jlmq.server.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.WebSocketSession;

public interface JlmqMessageService {

    void send(ObjectNode body, String queue);

    void checkMessagesFor(WebSocketSession session);

    void acknowledge(WebSocketSession session, String messageToken);

    void malformed(WebSocketSession session, String messageToken);

    void completed(WebSocketSession session, String messageToken);
}
