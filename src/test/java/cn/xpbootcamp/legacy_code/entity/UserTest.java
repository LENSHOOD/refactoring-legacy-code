package cn.xpbootcamp.legacy_code.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void should_generate_id_when_construct() {
        User user = new User();
        Assertions.assertThat(user.getId()).isNotEqualTo(0L);
    }
}