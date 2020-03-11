package cn.xpbootcamp.legacy_code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}