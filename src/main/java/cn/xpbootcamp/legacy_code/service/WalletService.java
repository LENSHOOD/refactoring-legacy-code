package cn.xpbootcamp.legacy_code.service;

public interface WalletService {
    boolean moveMoney(long buyerId, long sellerId, double amount);
}
