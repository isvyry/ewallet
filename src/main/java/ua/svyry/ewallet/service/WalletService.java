package ua.svyry.ewallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
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
public class WalletService {

    private final WalletRepository walletRepository;
    private final ConversionService conversionService;

    public Wallet createWallet(Customer customer) {
        Wallet builtWallet = Wallet.builder()
                .owner(customer)
                .createdDate(new Date(Instant.now().toEpochMilli()))
                .walletNumber(UUID.randomUUID())
                .build();
        return walletRepository.save(builtWallet);
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
