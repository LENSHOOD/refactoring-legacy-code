package cn.xpbootcamp.legacy_code.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class IdGeneratorTest {
    @Test
    void should_generate_uuid() {
        String uuid = IdGenerator.generateTransactionId();
        Assertions.assertThat(UUID.fromString(uuid).toString()).isEqualTo(uuid);
    }
}