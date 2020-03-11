package cn.xpbootcamp.legacy_code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.transaction.InvalidTransactionException;

class WalletTransactionTest {
    private WalletTransaction walletTransaction;
    private long buyerId = 1L;
    private long sellerId = 2L;
    private double amount = 10.0;
    private DistributedLock lock = mock(DistributedLock.class);
    private WalletService walletService = mock(WalletService.class);

    @BeforeEach
    public void init() {
        walletTransaction = new WalletTransaction(buyerId, sellerId, amount, lock, walletService);
    }

    @Test
    void should_throw_InvalidTransactionException_when_buyer_id_is_null() {
        walletTransaction = new WalletTransaction(null, sellerId, amount, lock, walletService);

        assertThatThrownBy(() -> walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
    }

    @Test
    void should_throw_InvalidTransactionException_when_seller_id_is_null() {
        walletTransaction = new WalletTransaction(buyerId, null, amount, lock, walletService);

        assertThatThrownBy(() -> walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
    }

    @Test
    void should_throw_InvalidTransactionException_when_amount_less_than_zero() {
        walletTransaction = new WalletTransaction(buyerId, sellerId, -1.0, lock, walletService);

        assertThatThrownBy(() -> walletTransaction.execute())
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
}