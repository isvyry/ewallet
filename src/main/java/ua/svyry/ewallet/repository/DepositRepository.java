package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Deposit;

@Component
public interface DepositRepository extends JpaRepository<Deposit, Long> {
}
