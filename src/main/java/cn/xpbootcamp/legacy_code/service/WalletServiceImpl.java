package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepository;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import java.util.Optional;

public class WalletServiceImpl implements WalletService {
    private UserRepository userRepository;

    public WalletServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> moveMoney(long buyerId, long sellerId, double amount) {
        User buyer = userRepository.find(buyerId);
        if (buyer.getBalance() < amount) {
            return Optional.empty();
        }

        User seller = userRepository.find(sellerId);
        seller.setBalance(seller.getBalance() + amount);
        buyer.setBalance(buyer.getBalance() - amount);
        return Optional.of(IdGenerator.generateTransactionId());
    }
}
