package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.entity.Card;
import ua.svyry.ewallet.entity.Customer;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SuspiciousActivityService {

    private final CustomerService customerService;
    private final ObjectProvider<TransactionService> transactionService;
    public void checkSuspiciousActivity(Card card) {
        Customer owner = card.getWallet().getOwner();
        int lastHourSuspiciousTransactions = transactionService
                .getIfAvailable().getLastHourSuspiciousTransactionsByCustomer(owner) + 1;

        if (lastHourSuspiciousTransactions >= 10) {
            owner.setBlockedForTransactions(true);
        } else if (lastHourSuspiciousTransactions >= 5) {
            owner.setSuspicious(true);
        }
    }

    public boolean isCustomerAllowedForTransactions(Customer customer) {
        return !customer.isBlockedForTransactions();
    }

}
