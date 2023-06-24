package ua.svyry.ewallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Customer;
import ua.svyry.ewallet.entity.Wallet;
import ua.svyry.ewallet.repository.WalletRepository;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    public Wallet createWallet(Customer customer) {
        Wallet builtWallet = Wallet.builder()
                .owner(customer)
                .walletNumber(UUID.randomUUID())
                .build();
        Wallet savedWallet = walletRepository.save(builtWallet);
        log.info(String.format("Created wallet[id: %s, number: %s, " +
        "ownerId: %s]", savedWallet.getId(),
                savedWallet.getWalletNumber().toString(), savedWallet.getOwner().getId()));
        return savedWallet;
    }

    public Wallet getById(Long id) {
        return walletRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cannot find wallet with id: %s", id)));
    }

    public Wallet getByCustomerId(Long customerId) {
        return walletRepository.findByOwner_Id(customerId).orElseThrow(
                () -> new EntityNotFoundException(String
                        .format("Cannot find wallet for customer with id: %s", customerId)));
    }
}
