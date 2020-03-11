package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import cn.xpbootcamp.legacy_code.vo.TransactionInfo;
import javax.transaction.InvalidTransactionException;
import java.util.Optional;

public class WalletTransaction {
    private TransactionInfo transactionInfo;

    private Long createdTimestamp;
    private STATUS status;
    private String transactionSerialNumber;

    private DistributedLock distributedLock;
    private WalletService walletService;

    public WalletTransaction(TransactionInfo transactionInfo, DistributedLock distributedLock, WalletService walletService)
            throws InvalidTransactionException {
        if (transactionInfo == null) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        this.transactionInfo = transactionInfo;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.distributedLock = distributedLock;
        this.walletService = walletService;
    }

    public boolean execute() {
        if (status == STATUS.EXECUTED) {
            return true;
        }
        boolean isLocked = tryLock();
        try {
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

            Optional<String> serialNumber = walletService.moveMoney(
                    transactionInfo.getBuyerId(), transactionInfo.getSellerId(), transactionInfo.getAmount());
            if (serialNumber.isPresent()) {
                this.transactionSerialNumber = serialNumber.get();
                this.status = STATUS.EXECUTED;
                return true;
            } else {
                this.status = STATUS.FAILED;
                return false;
            }
        } finally {
            unlock(isLocked);
        }
    }

    boolean tryLock() {
        if (!distributedLock.lock(String.valueOf(transactionInfo.getBuyerId()))) {
            return false;
        }

        boolean acquired = false;
        try {
            acquired = distributedLock.lock(String.valueOf(transactionInfo.getSellerId()));
            return acquired;
        } finally {
            if (!acquired) {
                distributedLock.unlock(String.valueOf(transactionInfo.getBuyerId()));
            }
        }
    }

    void unlock(boolean isLocked) {
        if (!isLocked) {
            return;
        }

        try {
            distributedLock.unlock(String.valueOf(transactionInfo.getSellerId()));
        } finally {
            distributedLock.unlock(String.valueOf(transactionInfo.getBuyerId()));
        }
    }

    public String getTransactionSerialNumber() {
        return transactionSerialNumber;
    }
}