package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Withdrawal;

@Component
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
