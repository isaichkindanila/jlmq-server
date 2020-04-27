package ru.itis.javalab.jlmq.server.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqMessageService;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSubscriptionService;

import java.io.IOException;

import static com.fasterxml.jackson.databind.node.JsonNodeType.OBJECT;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;

@Component
@AllArgsConstructor
public class JlmqHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final JlmqMessageService messageService;
    private final JlmqSubscriptionService subscriptionService;

    private String getString(JsonNode parent, String name) throws BadDataException {
        JsonNode node = parent.get(name);

        if (node == null || node.getNodeType() != STRING) {
            throw new BadDataException();
        }

        return node.asText();
    }

    private void handle(WebSocketSession session, String message) throws BadDataException {
        JsonNode root;

        try {
            root = mapper.readTree(message);
        } catch (JsonProcessingException e) {
            throw new BadDataException();
        }

        switch (getString(root, "command")) {
            // producer sends message
            case "send":
                JsonNode body = root.get("body");

                if (body == null) {
                    // missing body is equivalent to empty body
                    body = mapper.getNodeFactory().objectNode();
                } else if (body.getNodeType() != OBJECT) {
                    throw new BadDataException();
                }

                messageService.send((ObjectNode) body, getString(root, "queue"));
                break;

            // consumer subscribes to a queue
            case "subscribe":
                subscriptionService.subscribe(session, getString(root, "queue"));
                messageService.checkMessagesFor(session);
                break;

            // consumer acknowledges received message
            case "acknowledge":
                messageService.acknowledge(session, getString(root, "message"));
                break;

            // consumer can't process message because it's malformed
            case "malformed":
                messageService.malformed(session, getString(root, "message"));
                break;

            // consumer has completed processing message
            case "completed":
                messageService.completed(session, getString(root, "message"));
                break;

            // client error - unrecognized command
            default: throw new BadDataException();
        }
    }

    @Override
    @Synchronized
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            handle(session, message.getPayload());
        } catch (BadDataException e) {
            session.close(CloseStatus.BAD_DATA);
        } catch (Exception e) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    @Synchronized
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        subscriptionService.unsubscribe(session);
    }

    private static class BadDataException extends Exception {}
}
