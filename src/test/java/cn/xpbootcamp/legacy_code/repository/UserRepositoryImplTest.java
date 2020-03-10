package cn.xpbootcamp.legacy_code.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserRepositoryImplTest {
    @Test
    void should_throw_exception_when_call_find() {
        long fakeId = 1L;
        UserRepositoryImpl userRepository = new UserRepositoryImpl();

        Assertions.assertThatThrownBy(() -> userRepository.find(fakeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database server is connecting......");
    }
}