package cn.xpbootcamp.legacy_code.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletServiceImplTest {
    private User buyer;
    private User seller;
    private UserRepository userRepository = mock(UserRepository.class);
    private WalletServiceImpl walletService = new WalletServiceImpl(userRepository);

    @BeforeEach
    public void init() {
        buyer = new User();
        seller = new User();
    }

    @Test
    void should_return_false_when_buyer_cannot_hold_sufficient_money() {
        // given
        long fakeBuyerId = 1L;
        buyer.setBalance(0);
        when(userRepository.find(eq(fakeBuyerId))).thenReturn(buyer);

        // when
        boolean resukt = walletService.moveMoney(fakeBuyerId, 2L, 10);

        // then
        assertThat(resukt).isFalse();
    }

    @Test
    void should_return_true_when_buyer_move_10_to_seller() {
        // given
        long fakeBuyerId = 1L;
        long fakeSellerId = 2L;
        buyer.setBalance(10);
        seller.setBalance(0);
        when(userRepository.find(eq(fakeBuyerId))).thenReturn(buyer);
        when(userRepository.find(eq(fakeSellerId))).thenReturn(seller);

        // when
        boolean result = walletService.moveMoney(fakeBuyerId, fakeSellerId, 10);

        // then
        assertThat(result).isTrue();
        assertThat(buyer.getBalance()).isEqualTo(0L);
        assertThat(seller.getBalance()).isEqualTo(10L);
    }
}