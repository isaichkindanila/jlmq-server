package ru.itis.javalab.jlmq.server.services.interfaces;

public interface TokenGeneratorService {

    String hexToken(int bytes);
}
