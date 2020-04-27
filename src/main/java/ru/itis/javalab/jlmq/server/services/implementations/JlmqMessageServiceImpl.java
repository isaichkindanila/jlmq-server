package ru.itis.javalab.jlmq.server.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.itis.javalab.jlmq.server.models.Message;
import ru.itis.javalab.jlmq.server.models.Queue;
import ru.itis.javalab.jlmq.server.repositories.MessageRepository;
import ru.itis.javalab.jlmq.server.repositories.QueueRepository;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqMessageService;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSessionService;
import ru.itis.javalab.jlmq.server.services.interfaces.JlmqSubscriptionService;
import ru.itis.javalab.jlmq.server.services.interfaces.TokenGeneratorService;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JlmqMessageServiceImpl implements JlmqMessageService {

    private final ObjectMapper mapper;
    private final TokenGeneratorService tokenGeneratorService;

    private final JlmqSessionService sessionService;
    private final JlmqSubscriptionService subscriptionService;

    private final QueueRepository queueRepository;
    private final MessageRepository messageRepository;

    @Value("${token.message.bytes}")
    private int tokenByteLength;

    @SneakyThrows
    private TextMessage toTextMessage(Message message) {
        JsonNodeFactory factory = mapper.getNodeFactory();
        ObjectNode root = factory.objectNode();

        root.put("command", "receive");
        root.put("message", message.getToken());
        root.set("body", mapper.readTree(message.getBody()));

        return new TextMessage(root.toString());
    }

    @Override
    @SneakyThrows
    public void checkMessagesFor(WebSocketSession session) {
        if (!sessionService.hasQueue(session)) {
            return;
        }

        Queue queue = sessionService.getQueue(session);
        Optional<Message> optional = messageRepository.findFirstUnprocessed(queue);

        if (optional.isPresent()) {
            sessionService.setBusy(session);
            session.sendMessage(toTextMessage(optional.get()));
        } else {
            // no unprocessed messages - consumer is idle
            sessionService.setIdle(session);
        }
    }

    @Override
    public void send(ObjectNode body, String queueName) {
        Queue queue = queueRepository.findByName(queueName)
                .orElseThrow(IllegalArgumentException::new);

        Message message = messageRepository.save(Message.builder()
                .token(tokenGeneratorService.hexToken(tokenByteLength))
                .body(body.toString())
                .queue(queue)
                .sentAt(Instant.now())
                .status(Message.Status.RECEIVED)
                .build());

        subscriptionService.subscriber(queue).ifPresent(session -> {
            if (sessionService.isIdle(session)) {
                try {
                    session.sendMessage(toTextMessage(message));
                    sessionService.setBusy(session);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private void changeStatus(String token, Message.Status status) {
        Message message = messageRepository.findByToken(token)
                .orElseThrow(IllegalArgumentException::new);

        message.setStatus(status);
        messageRepository.save(message);
    }

    @Override
    public void acknowledge(WebSocketSession session, String messageToken) {
        changeStatus(messageToken, Message.Status.ACKNOWLEDGED);
    }

    @Override
    public void malformed(WebSocketSession session, String messageToken) {
        changeStatus(messageToken, Message.Status.MALFORMED);
        checkMessagesFor(session);
    }

    @Override
    public void completed(WebSocketSession session, String messageToken) {
        changeStatus(messageToken, Message.Status.COMPLETED);
        checkMessagesFor(session);
    }
}
