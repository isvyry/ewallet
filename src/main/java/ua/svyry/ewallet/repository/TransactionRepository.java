package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Transaction;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "Select count(tr.id) from transaction as tr\n" +
            "join card c on tr.card_id = c.id\n" +
            "join wallet w on w.id = c.wallet_id\n" +
            "join customer cus on cus.id = w.id\n" +
            "where cus.id = :customerId and tr.created_date > NOW() - interval '1 hour'", nativeQuery = true)
    int countAllByCustomerForTheLastHour(@Param("customerId") Long customerId);

}
