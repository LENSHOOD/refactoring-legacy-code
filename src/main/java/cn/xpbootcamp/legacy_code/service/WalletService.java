package cn.xpbootcamp.legacy_code.service;

import java.util.Optional;

public interface WalletService {
    Optional<String> moveMoney(long buyerId, long sellerId, double amount);
}
