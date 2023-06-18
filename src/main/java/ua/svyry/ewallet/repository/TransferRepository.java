package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Transfer;

@Component
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
