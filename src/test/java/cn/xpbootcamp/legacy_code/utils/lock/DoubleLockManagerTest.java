package cn.xpbootcamp.legacy_code.utils.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;

class DoubleLockManagerTest {
    private long id1 = 1L;
    private long id2 = 2L;
    private DistributedLock lock = mock(DistributedLock.class);
    private DoubleLockManager.DoubleLock doubleLock = new DoubleLockManager(lock).obtainLock(id1, id2);

    @Test
    void should_get_double_lock() {
        // given
        when(lock.lock(String.valueOf(id1))).thenReturn(true);
        when(lock.lock(String.valueOf(id2))).thenReturn(true);

        // when
        boolean isGetLock = doubleLock.tryLock();

        // then
        assertThat(isGetLock).isTrue();
    }

    @Test
    void should_return_false_when_get_buyer_lock_fail() {
        // given
        when(lock.lock(String.valueOf(id1))).thenReturn(false);

        // when
        boolean isGetLock = doubleLock.tryLock();

        // then
        assertThat(isGetLock).isFalse();
    }

    @Test
    void should_return_false_when_get_seller_lock_fail() {
        // given
        when(lock.lock(String.valueOf(id1))).thenReturn(true);
        when(lock.lock(String.valueOf(id2))).thenReturn(false);
        doNothing().when(lock).unlock(String.valueOf(id1));

        // when
        boolean isGetLock = doubleLock.tryLock();

        // then
        assertThat(isGetLock).isFalse();
        verify(lock, times(1)).unlock(String.valueOf(id1));
    }

    @Test
    void should_unlock() {
        // given
        doNothing().when(lock).unlock(String.valueOf(id1));
        doNothing().when(lock).unlock(String.valueOf(id2));

        // when
        doubleLock.unlock();

        // then
        verify(lock, times(1)).unlock(String.valueOf(id1));
        verify(lock, times(1)).unlock(String.valueOf(id2));
    }

    @Test
    void should_unlock_buyer_when_unlock_seller_fail() {
        // given
        doThrow(RuntimeException.class).when(lock).unlock(String.valueOf(id2));
        doNothing().when(lock).unlock(String.valueOf(id1));

        // when
        assertThatThrownBy(() -> doubleLock.unlock()).isInstanceOf(RuntimeException.class);

        // then
        verify(lock, times(1)).unlock(String.valueOf(id1));
    }
}