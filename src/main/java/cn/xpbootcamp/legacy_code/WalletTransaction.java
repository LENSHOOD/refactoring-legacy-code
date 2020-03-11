package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.repository.UserRepositoryImpl;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import javax.transaction.InvalidTransactionException;
import java.util.Optional;

public class WalletTransaction {
    private String transactionId;
    private Long buyerId;
    private Long sellerId;
    private Long createdTimestamp;
    private double amount;
    private STATUS status;
    private String transactionSerialNumber;
    private DistributedLock distributedLock;

    public WalletTransaction(Long buyerId, Long sellerId,
                             double amount, DistributedLock distributedLock) {
        this.transactionId = IdGenerator.generateTransactionId();
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.distributedLock = distributedLock;
    }

    public boolean execute() throws InvalidTransactionException {
        if (buyerId == null || (sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        if (status == STATUS.EXECUTED) {
            return true;
        }
        boolean isLocked = false;
        try {
            isLocked = distributedLock.lock(transactionId);

            // 锁定未成功，返回false
            if (!isLocked) {
                return false;
            }
            if (status == STATUS.EXECUTED) {
                return true; // double check
            }
            long executionInvokedTimestamp = System.currentTimeMillis();
            // 交易超过20天
            if (executionInvokedTimestamp - createdTimestamp > 1728000000) {
                this.status = STATUS.EXPIRED;
                return false;
            }
            WalletService walletService = new WalletServiceImpl(new UserRepositoryImpl());
            Optional<String> serialNumber = walletService.moveMoney(buyerId, sellerId, amount);
            if (serialNumber.isPresent()) {
                this.transactionSerialNumber = serialNumber.get();
                this.status = STATUS.EXECUTED;
                return true;
            } else {
                this.status = STATUS.FAILED;
                return false;
            }
        } finally {
            if (isLocked) {
                distributedLock.unlock(transactionId);
            }
        }
    }

    public String getTransactionSerialNumber() {
        return transactionSerialNumber;
    }
}