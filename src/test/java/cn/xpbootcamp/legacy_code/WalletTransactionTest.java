package cn.xpbootcamp.legacy_code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import cn.xpbootcamp.legacy_code.vo.TransactionInfo;
import cn.xpbootcamp.legacy_code.vo.exception.InvalidTransactionInfoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.transaction.InvalidTransactionException;

class WalletTransactionTest {
    private WalletTransaction walletTransaction;
    private long buyerId = 1L;
    private long sellerId = 2L;
    private double amount = 10.0;
    private TransactionInfo transactionInfo ;
    private DistributedLock lock = mock(DistributedLock.class);
    private WalletService walletService = mock(WalletService.class);

    @BeforeEach
    public void init() throws InvalidTransactionInfoException, InvalidTransactionException {
        transactionInfo = new TransactionInfo(buyerId, sellerId, amount);
        walletTransaction = new WalletTransaction(transactionInfo, lock, walletService);
    }

    @Test
    void should_throw_InvalidTransactionException_when_transaction_info_is_null() {
        assertThatThrownBy(() -> new WalletTransaction(null, lock, walletService))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
    }

    @Test
    void should_return_false_when_not_locked() throws InvalidTransactionException {
        // given
        when(lock.lock(anyString())).thenReturn(false);

        // when
        boolean result = walletTransaction.execute();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_get_double_lock() {
        // given
        when(lock.lock(String.valueOf(buyerId))).thenReturn(true);
        when(lock.lock(String.valueOf(sellerId))).thenReturn(true);

        // when
        boolean isGetLock = walletTransaction.tryLock();

        // then
        assertThat(isGetLock).isTrue();
    }

    @Test
    void should_return_false_when_get_buyer_lock_fail() {
        // given
        when(lock.lock(String.valueOf(buyerId))).thenReturn(false);

        // when
        boolean isGetLock = walletTransaction.tryLock();

        // then
        assertThat(isGetLock).isFalse();
    }

    @Test
    void should_return_false_when_get_seller_lock_fail() {
        // given
        when(lock.lock(String.valueOf(buyerId))).thenReturn(true);
        when(lock.lock(String.valueOf(sellerId))).thenReturn(false);
        doNothing().when(lock).unlock(String.valueOf(buyerId));

        // when
        boolean isGetLock = walletTransaction.tryLock();

        // then
        assertThat(isGetLock).isFalse();
        verify(lock, times(1)).unlock(String.valueOf(buyerId));
    }

    @Test
    void should_unlock() {
        // given
        doNothing().when(lock).unlock(String.valueOf(buyerId));
        doNothing().when(lock).unlock(String.valueOf(sellerId));

        // when
        walletTransaction.unlock();

        // then
        verify(lock, times(1)).unlock(String.valueOf(buyerId));
        verify(lock, times(1)).unlock(String.valueOf(sellerId));
    }

    @Test
    void should_unlock_buyer_when_unlock_seller_fail() {
        // given
        doThrow(RuntimeException.class).when(lock).unlock(String.valueOf(sellerId));
        doNothing().when(lock).unlock(String.valueOf(buyerId));

        // when
        assertThatThrownBy(() -> walletTransaction.unlock()).isInstanceOf(RuntimeException.class);

        // then
        verify(lock, times(1)).unlock(String.valueOf(buyerId));
    }

    @Test
    void should_execute_fail_when_expired() {
        // given
        WalletTransaction transaction = spy(walletTransaction);
        when(transaction.isExpired()).thenReturn(true);

        // when
        boolean result = transaction.execute();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_false_when_move_money_failed() {
        // given
        when(walletService.moveMoney(buyerId, sellerId, amount)).thenReturn(false);

        // when
        boolean result = walletTransaction.execute();

        // then
        assertThat(result).isFalse();
    }
}