package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.DistributedLock;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.vo.TransactionInfo;
import javax.transaction.InvalidTransactionException;
import java.util.Optional;

public class WalletTransaction {
    private TransactionInfo transactionInfo;

    private String transactionId;
    private Long createdTimestamp;
    private STATUS status;
    private String transactionSerialNumber;

    private DistributedLock distributedLock;
    private WalletService walletService;

    public WalletTransaction(TransactionInfo transactionInfo, DistributedLock distributedLock, WalletService walletService) throws InvalidTransactionException {
        if (transactionInfo == null) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        this.transactionInfo = transactionInfo;
        this.transactionId = IdGenerator.generateTransactionId();
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.distributedLock = distributedLock;
        this.walletService = walletService;
    }

    public boolean execute() {
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
            if (isLocked) {
                distributedLock.unlock(transactionId);
            }
        }
    }

    public String getTransactionSerialNumber() {
        return transactionSerialNumber;
    }
}