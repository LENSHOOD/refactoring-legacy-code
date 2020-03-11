package cn.xpbootcamp.legacy_code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.transaction.InvalidTransactionException;

class WalletTransactionTest {
    private WalletTransaction walletTransaction;
    private long buyerId = 1L;
    private long sellerId = 2L;
    private long productId = 3L;
    private double amount = 10.0;
    private String orderId = "OrderId";
    private String preAssignedId = "PreAssigned";
    private DistributedLock lock = mock(DistributedLock.class);

    @BeforeEach
    public void init() {
        walletTransaction = new WalletTransaction(preAssignedId, buyerId, sellerId, amount, lock);
    }

    @Test
    void should_throw_InvalidTransactionException_when_buyer_id_is_null() {
        walletTransaction = new WalletTransaction(preAssignedId, null, sellerId, amount, lock);

        assertThatThrownBy(() -> walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
    }

    @Test
    void should_throw_InvalidTransactionException_when_seller_id_is_null() {
        walletTransaction = new WalletTransaction(preAssignedId, buyerId, null, amount, lock);

        assertThatThrownBy(() -> walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
    }

    @Test
    void should_throw_InvalidTransactionException_when_amount_less_than_zero() {
        walletTransaction = new WalletTransaction(preAssignedId, buyerId, sellerId, -1.0, lock);

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