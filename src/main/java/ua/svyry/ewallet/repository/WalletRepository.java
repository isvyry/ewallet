package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Wallet;

import java.util.Optional;
import java.util.UUID;

@Component
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletNumber(UUID walletNumber);
    Optional<Wallet> findByOwner_Id(Long ownerId);
}
