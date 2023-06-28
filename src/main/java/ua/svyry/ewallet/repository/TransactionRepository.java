package ua.svyry.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.entity.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "Select count(tr.id) from transaction as tr\n" +
            "join card c on tr.card_id = c.id\n" +
            "join wallet w on w.id = c.wallet_id\n" +
            "join customer cus on cus.id = w.id\n" +
            "where cus.id = :customerId and tr.is_suspicious is true and tr.created_date > NOW() - interval '1 hour'",
            nativeQuery = true)
    int countAllSuspiciousByCustomerForTheLastHour(@Param("customerId") Long customerId);
    @Query(value = "select sum(tr.AMOUNT) from transaction tr\n" +
            "join card c on tr.card_id = c.id\n" +
            "join wallet w on w.id = c.wallet_id\n" +
            "join customer cus on cus.id = w.id\n" +
            "where tr.is_successful is true and c.id = :cardId and tr.transaction_type = 'Withdrawal' and tr.created_date > NOW() - interval '1 day'\n" +
            "group by cus.id\n" +
            "having cus.id = :customerId", nativeQuery = true)
    Optional<BigDecimal> selectDailyTransactionsAmountSummedUpByCustomerAndByCard(@Param("customerId") Long customerId,
                                                                                  @Param("cardId") Long cardId);

}
