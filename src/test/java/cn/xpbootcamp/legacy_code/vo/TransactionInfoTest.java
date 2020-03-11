package cn.xpbootcamp.legacy_code.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.xpbootcamp.legacy_code.vo.exception.InvalidTransactionInfoException;
import org.junit.jupiter.api.Test;

class TransactionInfoTest {
    private long buyerId = 1L;
    private long sellerId = 2L;
    private double amount = 10.0;
    
    @Test
    void should_throw_InvalidTransactionInfoException_when_buyer_id_is_null() {
        assertThatThrownBy(() -> new TransactionInfo(null, sellerId, amount))
                .isInstanceOf(InvalidTransactionInfoException.class)
                .hasMessage("This is an invalid transaction info");
    }

    @Test
    void should_throw_InvalidTransactionInfoException_when_seller_id_is_null() {
        assertThatThrownBy(() -> new TransactionInfo(buyerId, null, amount))
                .isInstanceOf(InvalidTransactionInfoException.class)
                .hasMessage("This is an invalid transaction info");
    }

    @Test
    void should_throw_InvalidTransactionInfoException_when_amount_less_than_zero() {
        assertThatThrownBy(() -> new TransactionInfo(buyerId, sellerId, -1.0))
                .isInstanceOf(InvalidTransactionInfoException.class)
                .hasMessage("This is an invalid transaction info");
    }
}