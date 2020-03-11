package cn.xpbootcamp.legacy_code.vo;

import static java.util.Objects.isNull;

import cn.xpbootcamp.legacy_code.vo.exception.InvalidTransactionInfoException;

/**
 * TransactionInfo:
 * @author zhangxuhai
 * @date 2020/3/12
*/
public class TransactionInfo {
    private Long buyerId;
    private Long sellerId;
    private double amount;

    public TransactionInfo(Long buyerId, Long sellerId, double amount) throws InvalidTransactionInfoException {
        if (isNull(buyerId) || isNull(sellerId) || amount <= 0.0) {
            throw new InvalidTransactionInfoException();
        }

        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public double getAmount() {
        return amount;
    }
}
