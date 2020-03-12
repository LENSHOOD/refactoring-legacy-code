package cn.xpbootcamp.legacy_code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.lock.DoubleLockManager;
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
    private DoubleLockManager lockManager = mock(DoubleLockManager.class);
    private DoubleLockManager.DoubleLock lock = mock(DoubleLockManager.DoubleLock.class);
    private WalletService walletService = mock(WalletService.class);

    @BeforeEach
    public void init() throws InvalidTransactionInfoException, InvalidTransactionException {
        when(lockManager.obtainLock(anyLong(), anyLong())).thenReturn(lock);
        transactionInfo = new TransactionInfo(buyerId, sellerId, amount);
        walletTransaction = new WalletTransaction(transactionInfo, lockManager, walletService);
    }

    @Test
    void should_throw_InvalidTransactionException_when_transaction_info_is_null() {
        assertThatThrownBy(() -> new WalletTransaction(null, lockManager, walletService))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessage("This is an invalid transaction");
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
        assertThat(transaction.getStatus()).isEqualTo(Status.EXPIRED);
    }

    @Test
    void should_return_false_when_not_locked() {
        // given
        when(lock.tryLock()).thenReturn(false);

        // when
        boolean result = walletTransaction.execute();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_false_when_move_money_failed() {
        // given
        when(lock.tryLock()).thenReturn(true);
        when(walletService.moveMoney(buyerId, sellerId, amount)).thenReturn(false);

        // when
        boolean result = walletTransaction.doMoveMoney();

        // then
        assertThat(result).isFalse();
        assertThat(walletTransaction.getStatus()).isEqualTo(Status.FAILED);
    }

    @Test
    void should_return_true_when_move_money_success() {
        // given
        when(lock.tryLock()).thenReturn(true);
        when(walletService.moveMoney(buyerId, sellerId, amount)).thenReturn(true);

        // when
        boolean result = walletTransaction.doMoveMoney();

        // then
        assertThat(result).isTrue();
        assertThat(walletTransaction.getStatus()).isEqualTo(Status.EXECUTED);
    }
}