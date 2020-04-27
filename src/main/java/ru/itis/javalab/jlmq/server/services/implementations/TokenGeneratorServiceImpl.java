package ru.itis.javalab.jlmq.server.services.implementations;

import org.springframework.stereotype.Service;
import ru.itis.javalab.jlmq.server.services.interfaces.TokenGeneratorService;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class TokenGeneratorServiceImpl implements TokenGeneratorService {

    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private final Random random = new SecureRandom();

    @Override
    public String hexToken(int bytes) {
        byte[] generated = new byte[bytes];
        random.nextBytes(generated);

        StringBuilder result = new StringBuilder();
        for (byte b : generated) {
            result.append(HEX[(b & 0xf0) >> 4]).append(HEX[b & 0x0f]);
        }

        return result.toString();
    }
}
