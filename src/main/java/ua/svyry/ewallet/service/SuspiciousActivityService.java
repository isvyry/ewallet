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
    private final int suspiciousLimit;

    @Value("${blocked-for-transactions.limit:10}")
    private final int blockedForTransactionsLimit;

    //We have to add 1 so total transaction count would be count() from DB + current transaction
    public void checkSuspiciousActivity(Card card) {
        Customer owner = card.getWallet().getOwner();
        int lastHourTransactionsCount = transactionService
                .getIfAvailable().getLastHourSuspiciousTransactionsByCustomer(owner) + 1;

        if (lastHourTransactionsCount >= blockedForTransactionsLimit) {
            log.warn(String.format("Customer has exceeded limit for creating transactions. Further transactions " +
                    "will not be posted. Blocking Customer[id: %s, email: %s] for transaction posting.",
                    owner.getId(), owner.getEmail()));
            owner.setBlockedForTransactions(true);
            owner.setSuspicious(true);
        } else if (lastHourTransactionsCount >= suspiciousLimit) {
            owner.setSuspicious(true);
            log.warn(String.format("Customer[id: %s, email: %s] has created more than %s transactions for the last hour." +
                            " The customer has been marked as suspicious.",
                    owner.getId(), owner.getEmail(), suspiciousLimit));
        }
    }

    public boolean isCustomerAllowedForTransactions(Customer customer) {
        if (customer.isBlockedForTransactions()) {
            log.info(String.format("Customer is blocked for creating transactions. Transaction could not be posted"));
            return false;
        } else return true;
    }

}
