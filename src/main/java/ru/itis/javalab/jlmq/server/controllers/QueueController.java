package ru.itis.javalab.jlmq.server.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itis.javalab.jlmq.server.dto.CreateQueueDto;
import ru.itis.javalab.jlmq.server.services.interfaces.QueueService;

@RestController
@AllArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/queue")
    public ResponseEntity<?> createQueue(@RequestBody CreateQueueDto dto) {
        try {
            queueService.create(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
