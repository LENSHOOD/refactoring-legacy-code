package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepository;

public class WalletServiceImpl implements WalletService {
    private UserRepository userRepository;

    public WalletServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean moveMoney(long buyerId, long sellerId, double amount) {
        User buyer = userRepository.find(buyerId);
        if (buyer.getBalance() < amount) {
            return false;
        }

        User seller = userRepository.find(sellerId);
        seller.setBalance(seller.getBalance() + amount);
        buyer.setBalance(buyer.getBalance() - amount);
        return true;
    }
}
