package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.utils.lock.DoubleLockManager;
import cn.xpbootcamp.legacy_code.vo.TransactionInfo;
import javax.transaction.InvalidTransactionException;

public class WalletTransaction {
    private static final int EXPIRED_INTERVAL = 1728000000;

    private TransactionInfo transactionInfo;
    private String id;
    private Status status = Status.TO_BE_EXECUTED;
    private Long createdTimestamp;

    private DoubleLockManager.DoubleLock lock;
    private WalletService walletService;

    public WalletTransaction(TransactionInfo transactionInfo, DoubleLockManager doubleLockManager, WalletService walletService)
            throws InvalidTransactionException {
        if (transactionInfo == null) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        this.transactionInfo = transactionInfo;
        this.id = IdGenerator.generateTransactionId();
        this.createdTimestamp = System.currentTimeMillis();
        this.walletService = walletService;
        this.lock = doubleLockManager.obtainLock(transactionInfo.getBuyerId(), transactionInfo.getSellerId());
    }

    public boolean execute() {
        if (isExpired()) {
            this.status = Status.EXPIRED;
            return false;
        }

        return doMoveMoney();
    }

    boolean doMoveMoney() {
        if (!lock.tryLock()) {
            return false;
        }
        try {
            boolean isMoneyMoved = walletService.moveMoney(
                    transactionInfo.getBuyerId(), transactionInfo.getSellerId(), transactionInfo.getAmount());
            if (!isMoneyMoved) {
                this.status = Status.FAILED;
                return false;
            }

            this.status = Status.EXECUTED;
            return true;
        } finally {
            lock.unlock();
        }
    }

    boolean isExpired() {
        return System.currentTimeMillis() - createdTimestamp > EXPIRED_INTERVAL;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}