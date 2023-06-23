package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Customer;

import java.util.Optional;

@Component
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("update Customer set isBlockedForTransactions = false where isBlockedForTransactions = true")
    @Modifying(flushAutomatically = true)
    int updateAllBlockedForTransactionsTrue();
}
