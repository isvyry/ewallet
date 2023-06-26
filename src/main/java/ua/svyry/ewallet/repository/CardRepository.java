package ua.svyry.ewallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Wallet;

@Component
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByWallet(Wallet wallet, Pageable pageable);
    boolean existsByCardNumberAndDeletedFalse(String cardNumber);
}
