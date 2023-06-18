package ua.svyry.ewallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.svyry.ewallet.shared.TransactionDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    public TransactionDto transferFunds(TransactionDto transactionDetails) {
        return null;
    }

    public TransactionDto depositFunds(TransactionDto transactionDetails) {
        return null;
    }

    public TransactionDto withdrawFunds(TransactionDto transactionDetails) {
        return null;
    }
}
