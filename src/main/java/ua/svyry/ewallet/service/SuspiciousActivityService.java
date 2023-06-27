package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Customer;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SuspiciousActivityService {

    private final ObjectProvider<TransactionService> transactionService;
    @Value("${suspicious.limit:5}")
    private int suspiciousLimit;

    @Value("${blocked-for-transactions.limit:10}")
    private int blockedForTransactionsLimit;

    //We have to add 1 so total suspicious transaction count would be count() from DB + current suspicious transaction
    public void checkSuspiciousActivity(Card card) {
        Customer owner = card.getWallet().getOwner();
        int lastHourSuspiciousTransactions = transactionService
                .getIfAvailable().getLastHourSuspiciousTransactionsByCustomer(owner) + 1;

        if (lastHourSuspiciousTransactions >= blockedForTransactionsLimit) {
            owner.setBlockedForTransactions(true);
            owner.setSuspicious(true);
        } else if (lastHourSuspiciousTransactions >= suspiciousLimit) {
            owner.setSuspicious(true);
        }
    }

    public boolean isCustomerAllowedForTransactions(Customer customer) {
        return !customer.isBlockedForTransactions();
    }

}
