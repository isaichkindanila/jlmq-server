package ru.itis.javalab.jlmq.server.models;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private String body;

    @Column(nullable = false, name = "sent_at")
    private Instant sentAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "queue_id")
    private Queue queue;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        RECEIVED, ACKNOWLEDGED, MALFORMED, COMPLETED
    }
}
